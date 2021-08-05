package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.config.security.SecurityUtilz;
import cn.nkpro.ts5.engine.co.NkCustomObjectManager;
import cn.nkpro.ts5.engine.doc.NkDocProcessor;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocFlowInterceptor;
import cn.nkpro.ts5.engine.doc.model.*;
import cn.nkpro.ts5.engine.doc.service.NkDocDefService;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.engine.task.NkBpmTaskService;
import cn.nkpro.ts5.exception.TfmsDefineException;
import cn.nkpro.ts5.orm.mb.gen.DocHMapper;
import cn.nkpro.ts5.orm.mb.gen.DocIExample;
import cn.nkpro.ts5.orm.mb.gen.DocIKey;
import cn.nkpro.ts5.orm.mb.gen.DocIMapper;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.LocalSyncUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NkDocEngineServiceImpl implements NkDocEngineFrontService {

    @Autowired@SuppressWarnings("all")
    private DocHMapper docHMapper;
    @Autowired@SuppressWarnings("all")
    private DocIMapper docIMapper;
    @Autowired@SuppressWarnings("all")
    private RedisSupport<DocHD> redisSupport;
    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private NkDocDefService docDefService;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskService bpmTaskService;


    @Override
    public DocHV create(String docType, String preDocId) {

        // 获取前序单据
        DocHV preDoc = StringUtils.isBlank(preDocId) || StringUtils.equalsIgnoreCase(preDocId,"@") ? null : detail(preDocId);

        // 获取单据配置
        DocDefHV def = docDefService.getDocDefForRuntime(docType);

        // 验证业务流
        validateFlow(def,preDoc);

        // 获取单据处理器
        NkDocProcessor processor = customObjectManager.getCustomObject(def.getRefObjectType(), NkDocProcessor.class);

        // 创建单据
        return processor.toCreate(def, preDoc);
    }


    @Override
    public DocHV detailView(String docId) {
        DocHV docHV = detail(docId);

        if(StringUtils.isNotBlank(docHV.getProcessInstanceId())){
            docHV.setBpmTask(
                    bpmTaskService.taskByBusinessAndAssignee(docId, SecurityUtilz.getUser().getId())
            );
        }

        return processViewDef(docHV);
    }

    @Override
    public DocHV detail(String docId) {

        // 获取单据抬头和行项目数据
        DocHD docHD = redisSupport.getIfAbsent(Constants.CACHE_DOC, docId,()->{

            DocHD doc = BeanUtilz.copyFromObject(docHMapper.selectByPrimaryKey(docId), DocHV.class);

            if(doc!=null){

                DocIExample example = new DocIExample();
                example.createCriteria()
                        .andDocIdEqualTo(docId);

                doc.setItems(docIMapper.selectByExampleWithBLOBs(example).stream()
                        .collect(Collectors.toMap(DocIKey::getCardKey, e -> e)));
            }

            return doc;
        });

        // 处理数据
        if(docHD != null){

            // 获取单据DEF
            DocDefHV def = docDefService.getDocDefForRuntime(docHD.getDocType());

            // 获取单据处理器 并执行
            return customObjectManager
                    .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                    .detail(def, docHD);
        }

        return null;
    }

    /**
     *
     * @throws IllegalTransactionStateException 在无transaction状态下执行；如果当前已有transaction，则抛出异常
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public DocHV calculate(DocHV doc, String fromCard, String options) {

        validate(doc);

        // 获取原始单据数据
        DocHV original = detail(doc.getDocId());

        // 获取单据配置
        DocDefHV def = Optional.ofNullable(original).map(DocHV::getDef).orElseGet(()->
                docDefService.getDocDefForRuntime(doc.getDocType())
        );

        // 获取单据处理器 并执行
        return customObjectManager
                .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                .calculate(doc, fromCard, options);
    }

    @Override
    @Transactional
    public DocHV doUpdateView(DocHV docHV){
        docHV = doUpdate(docHV);

        if(StringUtils.isNotBlank(docHV.getProcessInstanceId())){
            docHV.setBpmTask(
                    bpmTaskService.taskByBusinessAndAssignee(docHV.getDocId(), SecurityUtilz.getUser().getId())
            );
        }

        return processViewDef(docHV);
    }

    @Override
    @Transactional
    public DocHV doUpdate(DocHV doc) {

        validate(doc);

        // 获取原始单据数据
        Optional<DocHV> optionalOriginal = Optional.ofNullable(detail(doc.getDocId()));


        // 获取单据配置
        String docType = doc.getDocType();
        DocDefHV def = optionalOriginal.map(DocHV::getDef).orElseGet(()->
                docDefService.getDocDefForRuntime(docType)
        );

        if(!optionalOriginal.isPresent()){

            validateFlow(def,detail(doc.getPreDocId()));
        }

        try{
            doc.setDef(def);
            // 获取单据处理器 并执行
            return customObjectManager
                            .getCustomObject(def.getRefObjectType(), NkDocProcessor.class)
                            .doUpdate(doc, optionalOriginal.orElse(null),"用户操作");
        }finally {
            // 事务提交后清空缓存
            LocalSyncUtilz.runAfterCommit(()-> redisSupport.delete(Constants.CACHE_DOC, doc.getDocId()));
        }
    }

    @Override
    public void onBpmKilled(String docId, String processKey, String optSource) {
        DocHV docHV = detail(docId);
        customObjectManager.getCustomObject(docHV.getDef().getRefObjectType(), NkDocProcessor.class)
                .doOnBpmKilled(docHV, processKey, optSource);
        // 事务提交后清空缓存
        LocalSyncUtilz.runAfterCommit(()-> redisSupport.delete(Constants.CACHE_DOC, docId));
    }


    private void validate(DocHV doc){
        Assert.hasText(doc.getDocId(),"单据ID不能为空");
        Assert.hasText(doc.getDocType(),"单据类型不能为空");
    }


    private NkDocFlowInterceptor.FlowDescribe applyDocFlowInterceptor(String docFlowInterceptor, DocHV docHV){
        return customObjectManager.getCustomObject(docFlowInterceptor, NkDocFlowInterceptor.class)
                .apply(docHV);
    }

    /**
     * 验证业务流是否满足要求
     */
    private void validateFlow(DocDefHV def, DocHV preDoc){

        String preDocType = Optional.ofNullable(preDoc).map(DocHV::getDocType).orElse("@");
        String preDocState = Optional.ofNullable(preDoc).map(DocHV::getDocState).orElse("@");

        DocDefFlowV flowV = def.getFlows()
                .stream()
                .filter(item -> StringUtils.equals(item.getPreDocType(), preDocType))
                .findFirst()
                .orElseThrow(()->new TfmsDefineException("没有找到业务流配置"));

        if(!StringUtils.equals(flowV.getPreDocState(),preDocState)){
            throw new TfmsDefineException("状态不满足条件");
        }
        if(StringUtils.isNotBlank(flowV.getRefObjectType())){
            NkDocFlowInterceptor.FlowDescribe flowDescribe = applyDocFlowInterceptor(flowV.getRefObjectType(), preDoc);
            if(!flowDescribe.isVisible()){
                throw new TfmsDefineException(flowDescribe.getVisibleDesc());
            }
        }
    }

    /**
     * 处理可创建的后续单据类型
     */
    private DocHV processViewDef(DocHV docHV){

        Map<String, DocDefStateV> cache = new LinkedHashMap<>();
        docHV.getDef()
                .getStatus()
                .forEach(state->{
                    if(StringUtils.equalsAny(docHV.getDocState(),state.getPreDocState(),state.getDocState())){
                        cache.putIfAbsent(state.getDocState(),state);
                    }
                });
        docHV.getDef().setStatus(new ArrayList<>(cache.values()));

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

        return docHV;
    }
}
