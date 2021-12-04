/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.docengine.service.impl;

import cn.nkpro.easis.basic.Constants;
import cn.nkpro.easis.co.DebugContextManager;
import cn.nkpro.easis.co.NkCustomObjectManager;
import cn.nkpro.easis.co.spel.NkSpELManager;
import cn.nkpro.easis.data.elasticearch.SearchEngine;
import cn.nkpro.easis.data.redis.RedisSupport;
import cn.nkpro.easis.docengine.NkDocProcessor;
import cn.nkpro.easis.docengine.datasync.NkDocDataAdapter;
import cn.nkpro.easis.docengine.gen.*;
import cn.nkpro.easis.docengine.interceptor.NkDocFlowInterceptor;
import cn.nkpro.easis.docengine.model.*;
import cn.nkpro.easis.docengine.model.es.DocHES;
import cn.nkpro.easis.docengine.service.NkDocDefService;
import cn.nkpro.easis.docengine.service.NkDocEngineContext;
import cn.nkpro.easis.docengine.service.NkDocPermService;
import cn.nkpro.easis.exception.NkDefineException;
import cn.nkpro.easis.security.SecurityUtilz;
import cn.nkpro.easis.task.NkBpmTaskService;
import cn.nkpro.easis.utils.BeanUtilz;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
class AbstractNkDocEngine {


    @Autowired
    protected DocHMapper docHMapper;
    @Autowired
    protected DocIMapper docIMapper;
    @Autowired
    protected DocIIndexMapper docIIndexMapper;

    @Autowired
    protected NkDocDefService docDefService;
    @Autowired
    protected NkBpmTaskService bpmTaskService;
    @Autowired
    protected NkDocPermService docPermService;

    @Autowired
    protected RedisSupport<DocHV> redisRuntime;
    @Autowired
    protected RedisSupport<DocHPersistent> redisSupport;


    @Autowired
    protected SearchEngine searchEngine;
    @Autowired
    protected NkSpELManager spELManager;
    @Autowired
    protected DebugContextManager debugContextManager;
    @Autowired
    protected NkCustomObjectManager customObjectManager;

    //protected void info(String message,Object... params){
    //    log.info(NkDocEngineContext.currLog() + message, params);
    //}


    /**
     * 获取单据的持久化对象
     */
    DocHPersistent fetchDoc(String docId){
        if(log.isInfoEnabled())log.info("{}获取单据原始数据", NkDocEngineContext.currLog());

        // 获取单据抬头和行项目数据
        final long start = System.currentTimeMillis();

        try{
            DocHPersistent doc = null;

            // 尝试冲调试上下文获取
            if(debugContextManager.isDebug()){
                doc = debugContextManager.getDebugResource("$"+docId);
            }

            if(doc==null){
                // 尝试从缓存获取
                doc = redisSupport.getIfAbsent(
                        Constants.CACHE_DOC,
                        docId,
                        ()-> fetchDocFromDB(docId)
                );
            }

            return doc;

        }finally {
            if(log.isInfoEnabled())
                log.info("{}获取单据原始数据 耗时{}ms", NkDocEngineContext.currLog(), System.currentTimeMillis()-start);
        }
    }

    /**
     * 从数据库中加载单据
     */
    private DocHPersistent fetchDocFromDB(String docId){

        DocHPersistent doc = BeanUtilz.copyFromObject(docHMapper.selectByPrimaryKey(docId), DocHPersistent.class);

        if(doc == null)
            return null;

        DocIExample example = new DocIExample();
        example.createCriteria()
                .andDocIdEqualTo(docId);

        doc.setItems(docIMapper.selectByExampleWithBLOBs(example).stream()
                .collect(Collectors.toMap(DocIKey::getCardKey, e -> e)));

        DocIIndexExample iIndexExample = new DocIIndexExample();
        iIndexExample.createCriteria()
                .andDocIdEqualTo(docId);
        iIndexExample.setOrderByClause("ORDER_BY");

        docIIndexMapper.selectByExample(iIndexExample).forEach(docIIndex -> {
            try {
                doc.getDynamics().put(docIIndex.getName(), JSON.parseObject(docIIndex.getValue(),Class.forName(docIIndex.getDataType())));
            } catch (ClassNotFoundException ex) {
                doc.getDynamics().put(docIIndex.getName(),null);
            }
        });

        return doc;
    }

    /**
     * 将持久化单据对象转化为DocHV对象
     * 实质是执行单据处理器的detail，将序列化的卡片数据转反序列化成对象格式
     */
    DocHV persistentToHV(DocHPersistent docHPersistent){

        // 处理数据
        if(docHPersistent != null){

            // 获取单据DEF
            DocDefHV def = NkDocEngineContext.localDef(
                    docHPersistent.getDocType(),
                    (docType)->docDefService.getDocDefForRuntime(docType)
            );

            // 获取单据处理器 并执行
            NkDocProcessor docProcessor = customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class);

            if(log.isInfoEnabled())
                log.info("{}确定单据处理器 = {}", NkDocEngineContext.currLog(), docProcessor.getBeanName());

            return docProcessor.detail(def, docProcessor.deserialize(def, docHPersistent));
        }
        return null;
    }

    /**
     *
     * 将单据对象做进一步加工，使之符合在UI上展示
     *
     */
    DocHV docToView(DocHV docHV){

        // 根据权限过滤单据数据与配置
        docPermService.filterDocCards(null, docHV);

        // 加载活动的bpm任务实例
        if(StringUtils.isNotBlank(docHV.getProcessInstanceId())){
            docHV.setBpmTask(
                    bpmTaskService.taskByBusinessAndAssignee(docHV.getDocId(), SecurityUtilz.getUser().getId())
            );
            if(log.isInfoEnabled())log.info("{}获取单据任务", NkDocEngineContext.currLog());
        }

        // 根据当前状态，展示可见的操作状态
        Map<String, DocDefStateV> cache = new LinkedHashMap<>();
        docHV.getDef()
                .getStatus()
                .forEach(state->{
                    if(StringUtils.equalsAny(docHV.getDocState(),state.getPreDocState(),state.getDocState())){
                        cache.putIfAbsent(state.getDocState(),state);
                    }
                });
        docHV.getDef().setStatus(new ArrayList<>(cache.values()));
        if(log.isInfoEnabled())
            log.info("{}设置单据可用状态 状态 = {}",
                    NkDocEngineContext.currLog(),
                    docHV.getDef().getStatus()
            );

        // 设置单据可见的业务流
        docHV.getDef()
                .getNextFlows()
                .forEach(flow->{
                    String[] splitState = StringUtils.split(flow.getPreDocState(), ',');
                    boolean visibleState = ArrayUtils.contains(splitState,docHV.getDocState()) || ArrayUtils.contains(splitState,"@");

                    if(!visibleState){
                        flow.setVisibleDesc("状态不满足条件");
                    }

                    // 处理 单据业务流的自定义条件
                    if(visibleState && StringUtils.isNotBlank(flow.getRefObjectType())){
                        NkDocFlowInterceptor.FlowDescribe flowDescribe = applyDocFlowInterceptor(flow.getRefObjectType(), docHV);
                        if(!flowDescribe.isVisible()){
                            flow.setVisibleDesc(flowDescribe.getVisibleDesc());
                        }
                    }

                    flow.setVisible(visibleState);
                });

        if(log.isInfoEnabled())
            log.info("{}设置单据后续操作 操作数量 = {}",
                    NkDocEngineContext.currLog(),
                    docHV.getDef().getNextFlows().size()
            );

        // 每一次操作，都延长单据运行时缓存的有效期
        if(StringUtils.isNotBlank(docHV.getRuntimeKey())){
            redisRuntime.set(docHV.getRuntimeKey(),docHV);
            redisRuntime.expire(docHV.getRuntimeKey(),60 * 60);//1小时
        }

        return docHV;
    }

    /**
     * 保存成功后，移除runtime标示
     */
    void runtimeClear(DocHV doc){
        if(StringUtils.isNotBlank(doc.getRuntimeKey())){
            redisRuntime.delete(doc.getRuntimeKey());
            doc.setRuntimeKey(null);
        }
    }


    /**
     * 验证业务流是否满足要求
     */
    void validateFlow(DocDefHV def, DocHV preDoc){

        long now = System.currentTimeMillis();

        String preDocType = Optional.ofNullable(preDoc).map(DocHV::getDocType).orElse("@");
        String preDocState = Optional.ofNullable(preDoc).map(DocHV::getDocState).orElse("@");

        if(log.isInfoEnabled())log.info("{}验证单据业务流 docType = {} prevDocType = {}", NkDocEngineContext.currLog(), def.getDocType(), preDocType);

        DocDefFlowV flowV = def.getFlows()
                .stream()
                .filter(item -> StringUtils.equals(item.getPreDocType(), preDocType))
                .findFirst()
                .orElseThrow(()->new NkDefineException("没有找到业务流配置"));

        if(!StringUtils.equalsAny(flowV.getPreDocState(),preDocState, "@")){
            throw new NkDefineException("状态不满足条件");
        }
        if(StringUtils.isNotBlank(flowV.getRefObjectType())){
            NkDocFlowInterceptor.FlowDescribe flowDescribe = applyDocFlowInterceptor(flowV.getRefObjectType(), preDoc);
            if(!flowDescribe.isVisible()){
                throw new NkDefineException(flowDescribe.getVisibleDesc());
            }
        }
        if(log.isInfoEnabled())
            log.info("{}验证单据业务流 docType = {} 完成 耗时{}ms",
                    NkDocEngineContext.currLog(),
                    def.getDocType(),
                    System.currentTimeMillis() - now
            );
    }

    private NkDocFlowInterceptor.FlowDescribe applyDocFlowInterceptor(String docFlowInterceptor, DocHV docHV){
        return customObjectManager.getCustomObject(docFlowInterceptor, NkDocFlowInterceptor.class)
                .apply(docHV);
    }

    void validate(DocHV doc){
        Assert.hasText(doc.getDocId(),"单据ID不能为空");
        Assert.hasText(doc.getDocType(),"单据类型不能为空");
    }


    void execDataSync(DocHV doc, DocHV original){
        long start1 = System.currentTimeMillis();
        dataSync(doc, original, false);
        if(log.isInfoEnabled())log.info("{}保存单据 同步数据 耗时{}ms", NkDocEngineContext.currLog(), System.currentTimeMillis() - start1);
        long start2 = System.currentTimeMillis();
        searchEngine.indexBeforeCommit(DocHES.from(doc));
        if(log.isInfoEnabled())log.info("{}保存单据 更新索引 耗时{}ms", NkDocEngineContext.currLog(), System.currentTimeMillis() - start2);
    }

    /**
     * todo 如果 doc 和 original是同一个对象，可以优化性能
     */
    void dataSync(DocHV doc, DocHV original, boolean reExecute){

        EvaluationContext context1 = spELManager.createContext(doc);
        EvaluationContext context2 = spELManager.createContext(original);

        if(doc.getDef().getDataSyncs()!=null){

            doc.getDef().getDataSyncs().forEach(config -> {
                if(!reExecute || config.getReExecute() == 1){
                    // 满足前置条件
                    if(StringUtils.isBlank(config.getConditionSpEL())||
                            (Boolean) spELManager.invoke(config.getConditionSpEL(),context1)){

                        customObjectManager.getCustomObject(config.getTargetSvr(), NkDocDataAdapter.class)
                                .sync(doc, original, context1, context2, config);
                    }
                }
            });
        }
    }
}
