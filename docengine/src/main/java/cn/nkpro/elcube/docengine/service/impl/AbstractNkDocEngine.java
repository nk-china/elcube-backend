/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.docengine.service.impl;

import cn.nkpro.elcube.basic.Constants;
import cn.nkpro.elcube.basic.TransactionSync;
import cn.nkpro.elcube.co.DebugContextManager;
import cn.nkpro.elcube.co.NkCustomObjectManager;
import cn.nkpro.elcube.co.spel.NkSpELManager;
import cn.nkpro.elcube.data.elasticearch.SearchEngine;
import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.docengine.NkDocEngineThreadLocal;
import cn.nkpro.elcube.docengine.NkDocProcessor;
import cn.nkpro.elcube.docengine.datasync.NkDocDataAdapter;
import cn.nkpro.elcube.docengine.gen.*;
import cn.nkpro.elcube.docengine.interceptor.NkDocFlowInterceptor;
import cn.nkpro.elcube.docengine.interceptor.NkDocStateInterceptor;
import cn.nkpro.elcube.docengine.model.*;
import cn.nkpro.elcube.docengine.model.es.DocHES;
import cn.nkpro.elcube.docengine.service.NkDocDefService;
import cn.nkpro.elcube.docengine.service.NkDocPermService;
import cn.nkpro.elcube.exception.NkDefineException;
import cn.nkpro.elcube.exception.NkOperateNotAllowedCaution;
import cn.nkpro.elcube.exception.NkSystemException;
import cn.nkpro.elcube.security.SecurityUtilz;
import cn.nkpro.elcube.task.NkBpmTaskService;
import cn.nkpro.elcube.utils.BeanUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
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
        if(log.isInfoEnabled())log.info("获取单据原始数据");

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
                log.info("获取单据原始数据 耗时{}ms", System.currentTimeMillis()-start);
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

        Map<String,List<Object>> dynamics = new HashMap<>();
        docIIndexMapper.selectByExample(iIndexExample)
                .forEach(docIIndex ->
                    dynamics.compute(docIIndex.getName(),(key,value)->{
                        if(value==null)
                            value = new ArrayList<>();

                        if(String.class.getName().equals(docIIndex.getDataType())){
                            value.add(docIIndex.getValue());
                        }else if(Number.class.getName().equals(docIIndex.getDataType())){
                            value.add(docIIndex.getNumberValue());
                        }else{
                            value.add(null);
                        }

//                        try {
//                            value.add(JSON.parseObject(docIIndex.getValue(),Class.forName(docIIndex.getDataType())));
//                        } catch (ClassNotFoundException ignored) {
//                        }
                        return value;
                    })
                );
        dynamics.forEach((k,v)->{
            if(v.size()>1){
                doc.getDynamics().put(k,v);
            }else{
                doc.getDynamics().put(k,v.get(0));
            }
        });

//        docIIndexMapper.selectByExample(iIndexExample).forEach(docIIndex -> {
//            try {
//                doc.getDynamics().put(docIIndex.getName(), JSON.parseObject(docIIndex.getValue(),Class.forName(docIIndex.getDataType())));
//            } catch (ClassNotFoundException ex) {
//                doc.getDynamics().put(docIIndex.getName(),null);
//            }
//        });

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
            DocDefHV def = NkDocEngineThreadLocal.localDef(
                    docHPersistent.getDocType(),
                    (docType)->docDefService.getDocDefForRuntime(docType)
            );

            // 获取单据处理器 并执行
            NkDocProcessor docProcessor = customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class);

            if(log.isInfoEnabled())
                log.info("确定单据处理器 = {}", docProcessor.getBeanName());

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

        // 每一次操作，都延长单据运行时缓存的有效期
        if(StringUtils.isNotBlank(docHV.getRuntimeKey())){
            redisRuntime.set(docHV.getRuntimeKey(),docHV);
            redisRuntime.expire(docHV.getRuntimeKey(),60 * 60);//1小时
        }

        // 根据权限过滤单据数据与配置
        docPermService.filterDocCards("READ", docHV);

        // 加载活动的bpm任务实例
        if(StringUtils.isNotBlank(docHV.getProcessInstanceId())){
            docHV.setBpmTask(
                    bpmTaskService.taskByBusinessAndAssignee(docHV.getDocId(), SecurityUtilz.getUser().getId())
            );
            if(log.isInfoEnabled())log.info("获取单据任务");
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
        cache.values()
                .forEach(state->{
                    state.setVisible(true);
                    if(StringUtils.isNotBlank(state.getRefObjectType())){
                        state.setVisible(
                            customObjectManager.getCustomObject(state.getRefObjectType(), NkDocStateInterceptor.class)
                                .apply(docHV, state)
                        );
                    }
                });
        docHV.getDef().setStatus(new ArrayList<>(cache.values()));
        if(log.isInfoEnabled())
            log.info("设置单据可用状态 状态 = {}",
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
                        NkDocFlowInterceptor.FlowDescribe flowDescribe = applyDocFlowInterceptor(flow, docHV);
                        if(!flowDescribe.isVisible()){
                            visibleState = false;
                            flow.setVisibleDesc(flowDescribe.getVisibleDesc());
                        }
                    }

                    flow.setVisible(visibleState);
                });

        if(log.isInfoEnabled())
            log.info("设置单据后续操作 操作数量 = {}",
                    docHV.getDef().getNextFlows().size()
            );

        EvaluationContext context = spELManager.createContext(docHV);

        // 处理自定义卡片的显示逻辑
        docHV.getDef()
                .getCards()
                .removeIf(defIV -> {
                    if(StringUtils.isNotBlank(defIV.getVisibleSpEL())){
                        Boolean visible = (Boolean) spELManager.invoke(defIV.getVisibleSpEL(), context);
                        if(visible!=null && !visible){
                            if(log.isInfoEnabled())
                                        log.info("设置卡片{} 不显示",
                                        defIV.getCardKey()
                                );
                            return true;
                        }
                    }
                    return false;
                });

        // 处理自定义卡片的编辑逻辑
        docHV.getDef()
                .getCards()
                .forEach(defIV -> {
                    if(StringUtils.isNotBlank(defIV.getEditableSpEL())){
                        Boolean editable = (Boolean) spELManager.invoke(defIV.getEditableSpEL(), context);
                        if(editable!=null && !editable){
                            if(log.isInfoEnabled())
                                log.info("设置卡片{} 只读",
                                        defIV.getCardKey()
                                );
                            defIV.setWriteable(false);
                        }
                    }
                });

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

        if(log.isInfoEnabled())log.info("验证单据业务流 docType = {} prevDocType = {}", def.getDocType(), preDocType);

        DocDefFlowV flowV = def.getFlows()
                .stream()
                .filter(item -> StringUtils.equals(item.getPreDocType(), preDocType))
                .findFirst()
                .orElseThrow(()->new NkDefineException("没有找到业务流配置"));

        if(!StringUtils.equalsAny(flowV.getPreDocState(),preDocState, "@")){
            throw new NkDefineException("状态不满足条件");
        }
        if(StringUtils.isNotBlank(flowV.getRefObjectType())){
            NkDocFlowInterceptor.FlowDescribe flowDescribe = applyDocFlowInterceptor(flowV, preDoc);
            if(!flowDescribe.isVisible()){
                throw new NkDefineException(flowDescribe.getVisibleDesc());
            }
        }
        if(log.isInfoEnabled())
            log.info("验证单据业务流 docType = {} 完成 耗时{}ms",
                    def.getDocType(),
                    System.currentTimeMillis() - now
            );
    }

    private NkDocFlowInterceptor.FlowDescribe applyDocFlowInterceptor(DocDefFlow flow, DocHV docHV){
        return customObjectManager.getCustomObject(flow.getRefObjectType(), NkDocFlowInterceptor.class)
                .apply(docHV, flow);
    }

    void validate(DocHV doc){
        Assert.hasText(doc.getDocId(),"单据ID不能为空");
        Assert.hasText(doc.getDocType(),"单据类型不能为空");
    }


    void execDataSync(DocHV doc, DocHV original){
        long start1 = System.currentTimeMillis();
        dataSync(doc, original, false);
        if(log.isInfoEnabled())log.info("保存单据 同步数据 耗时{}ms", System.currentTimeMillis() - start1);
        long start2 = System.currentTimeMillis();
        searchEngine.indexBeforeCommit(DocHES.from(doc));
        if(log.isInfoEnabled())log.info("保存单据 更新索引 耗时{}ms", System.currentTimeMillis() - start2);
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

    DocHV lockDocDo(String docId, Function<String,DocHV> function){
        String lockedValue;

        // 尝试锁定单据1小时
        int i = 0;
        do{
            lockedValue = redisSupport.lock(docId, 3600);

            if(lockedValue!=null)
                break;

            log.debug("尝试获取锁[{}]失败，{}ms后重试", docId, 1000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new NkSystemException(e.getMessage(),e);
            }
        }while (++i <= 10);

        // 获取锁失败
        if(lockedValue==null){
            log.warn("单据{} 被其他用户锁定，请稍后再试", docId);
            throw new NkOperateNotAllowedCaution("单据被其他用户锁定，请稍后再试");
        }

        // 获取锁成功
        try{
            return function.apply(docId);
        }finally {
            String finalDocId = docId;
            String finalLockedValue = lockedValue;
            TransactionSync.runAfterCompletionLast("解除Redis锁",(status)->
                    redisSupport.unlock(finalDocId, finalLockedValue)
            );
        }
    }
}
