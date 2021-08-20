package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.config.id.GUID;
import cn.nkpro.ts5.config.id.SequenceSupport;
import cn.nkpro.ts5.engine.co.NkCustomObjectManager;
import cn.nkpro.ts5.engine.doc.NkCard;
import cn.nkpro.ts5.engine.doc.NkDocCycle;
import cn.nkpro.ts5.engine.doc.NkDocProcessor;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocCommittedInterceptor;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocCreateInterceptor;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocExecuteInterceptor;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocUpdateInterceptor;
import cn.nkpro.ts5.engine.doc.model.*;
import cn.nkpro.ts5.engine.doc.service.NkDocDefService;
import cn.nkpro.ts5.engine.doc.service.NkDocHistoryService;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.engine.task.NkBpmTaskService;
import cn.nkpro.ts5.orm.mb.gen.*;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.ts5.utils.LocalSyncUtilz;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unchecked")
@Component("NKDocTransactionProcessor")
public class NkDocTransactionProcessor implements NkDocProcessor {

    @Autowired@SuppressWarnings("all")
    protected GUID guid;
    @Autowired@SuppressWarnings("all")
    private NkDocDefService docDefService;
    @Autowired@SuppressWarnings("all")
    private SequenceSupport sequenceUtils;
    @Autowired@SuppressWarnings("all")
    private DocHMapper docHMapper;
    @Autowired@SuppressWarnings("all")
    private DocIMapper docIMapper;
    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskService bpmTaskService;
    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private NkDocHistoryService docHistoryService;

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.TRANSACTION;
    }
    @Override
    public String desc() {
        return "交易";
    }

    @Override
    public DocHV toCreate(DocDefHV def, DocHV preDoc) {

        Optional<DocHV> optPreDoc = Optional.ofNullable(preDoc);

        DocHV doc = new DocHV();

        // 核心字段
        doc.setDocId(guid.nextId(DocH.class));
        doc.setClassify(def.getDocClassify());
        doc.setDocType(def.getDocType());
        doc.setDocName(def.getDocName());
        doc.setDocState(def.getStatus().get(0).getDocState());

        // 关联字段
        doc.setRefObjectId(optPreDoc.map(DocHV::getRefObjectId).orElse(doc.getDocId()));
        doc.setPreDocId(optPreDoc.map(DocHV::getRefObjectId).orElse("@"));

        // 系统字段
        doc.setCreatedTime(DateTimeUtilz.nowSeconds());
        doc.setDefVersion(def.getVersion());

        // 引用数据
        //doc.setPreDoc(preDoc);
        doc.setDef(def);

        DocHV loopDoc = processCycle(doc, NkDocCycle.beforeCreate, (beanName)->
                customObjectManager
                        .getCustomObject(beanName, NkDocCreateInterceptor.class)
                        .apply(doc,preDoc, NkDocCycle.beforeCreate)
        );

        docDefService.runLoopCards(def,false, (card, defIV)->
                loopDoc.getData().put(
                defIV.getCardKey(),
                card.afterCreate(loopDoc,preDoc,card.deserialize(null), defIV, defIV.getConfig())
            )
        );

        return processCycle(doc, NkDocCycle.afterCreated, (beanName)->
                customObjectManager
                        .getCustomObject(beanName, NkDocCreateInterceptor.class)
                        .apply(doc,preDoc, NkDocCycle.afterCreated)
        );
    }

    @Override
    public Object call(DocHV doc, String fromCard, String method, String options) {

        docDefService.runLoopCards(doc.getDef(),false, (card, defIV)->
                doc.getData().put(
                        defIV.getCardKey(),
                        card.deserialize(doc.getData().get(defIV.getCardKey()))
                )
        );

        DocDefIV docDefI = doc.getDef().getCards()
                .stream()
                .filter(card -> StringUtils.equals(card.getCardKey(), fromCard))
                .findFirst()
                .orElse(null);

        if(docDefI!=null){
            return customObjectManager.getCustomObject(docDefI.getBeanName(), NkCard.class)
                    .call(
                            doc,
                            doc.getData().get(docDefI.getCardKey()),
                            docDefI,
                            docDefI.getConfig(),
                            options
                    );
        }

        return null;
    }

    @Override
    public DocHV calculate(DocHV doc, String fromCard, String options){

        docDefService.runLoopCards(doc.getDef(),false, (card, defIV)->
                doc.getData().put(
                    defIV.getCardKey(),
                    card.deserialize(doc.getData().get(defIV.getCardKey()))
            )
        );

        DocHV loopDoc = processCycle(doc, NkDocCycle.beforeCalculate, (beanName)->
                customObjectManager
                        .getCustomObject(beanName, NkDocExecuteInterceptor.class)
                        .apply(doc, NkDocCycle.beforeCalculate)
        );

        docDefService.runLoopCards(loopDoc.getDef(),false, (card, defIV)->{

            // 是否为触发计算的卡片
            boolean isTrigger = StringUtils.equals(fromCard,defIV.getCardKey());

            // 获取计算函数的自定义选项
            String itemOptions = isTrigger?options:null;

            Object cardData = loopDoc.getData().get(defIV.getCardKey());

            loopDoc.getData().put(
                    defIV.getCardKey(),
                    card.calculate(loopDoc, cardData, defIV, defIV.getConfig(), isTrigger, itemOptions)
            );
        });

        return processCycle(doc, NkDocCycle.afterCalculated, (beanName)->
                customObjectManager
                        .getCustomObject(beanName, NkDocExecuteInterceptor.class)
                        .apply(doc, NkDocCycle.afterCalculated)
        );
    }

    @Override
    public DocHV detail(DocDefHV def, DocHPersistent docHD) {

        if(log.isInfoEnabled())log.info("获取单据内容 docId = {}: 处理器开始", docHD.getDocId());

        DocHV doc = BeanUtilz.copyFromObject(docHD, DocHV.class);
        doc.setDef(def);

        // 解析单据行项目数据
        if(log.isInfoEnabled())log.info("获取单据内容 docId = {}: 处理单据卡片数据", docHD.getDocId());
        docDefService.runLoopCards(def,false, (nkCard, defIV)->{

            // 获取行项目数据
            DocI docI = doc.getItems().computeIfAbsent(defIV.getCardKey(),(key)->{
                DocIV n = new DocIV();
                n.setCardKey(key);
                // warning: docId 作为保存时 insert 或 update 的判断，所以这里不要赋值
                // n.setDocId(doc.getDocId());
                return n;
            });

            doc.getData().put(
                defIV.getCardKey(),
                nkCard.afterGetData(doc, nkCard.deserialize(docI.getCardContent()), defIV, defIV.getConfig())
            );

            // 清除JSON，避免无效数据网络传输
            docI.setCardContent(null);
        });

        //docHES.setTags(StringUtils.split(docBO.getDocTags(),','));
        doc.setDocTypeDesc(String.format("%s | %s",doc.getDocType(),doc.getDef().getDocName()));
        doc.getDef().getStatus().stream()
                .filter(defDocStatus -> StringUtils.equals(defDocStatus.getDocState(),doc.getDocState()))
                .findAny().ifPresent(state ->
                        doc.setDocStateDesc(String.format("%s | %s",state.getDocState(),state.getDocStateDesc()))
                );

        if(log.isInfoEnabled())log.info("获取单据内容 docId = {}: 完成", docHD.getDocId());

        return doc;
    }

    private boolean existsOriginal(DocHV original, DocDefIV defIV){
        return original !=null
                && original.getItems().containsKey(defIV.getCardKey())
                && StringUtils.isNotBlank(original.getItems().get(defIV.getCardKey()).getDocId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocHV doUpdate(DocHV doc, DocHV original, String optSource) {

        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 处理器开始", doc.getDocId());

        // 原始单据
        Optional<DocHV> optionalOriginal = Optional.ofNullable(original);

        doc.setUpdatedTime(DateTimeUtilz.nowSeconds());

        if(!optionalOriginal.isPresent()){
            if(StringUtils.isBlank(doc.getDocNumber()))
                doc.setDocNumber(sequenceUtils.next(EnumDocClassify.valueOf(doc.getClassify()), doc.getDocType()));

            doc.setCreatedTime(doc.getUpdatedTime());
        }

        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 开始处理单据卡片数据", doc.getDocId());
        docDefService.runLoopCards(doc.getDef(),false, (card, defIV)->
                doc.getData().put(
                        defIV.getCardKey(),
                        card.deserialize(doc.getData().get(defIV.getCardKey()))
                )
        );
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 反序列化数据完成", doc.getDocId());

        DocHV loopDoc = processCycle(doc, NkDocCycle.beforeUpdate, (beanName)->
                customObjectManager
                        .getCustomObject(beanName, NkDocUpdateInterceptor.class)
                        .apply(doc, original, NkDocCycle.beforeUpdate)
        );
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 触发单据 beforeUpdate 接口", doc.getDocId());

        docDefService.runLoopCards(loopDoc.getDef(),false, (card, defIV)->{

            boolean existsOriginal = existsOriginal(original, defIV);

            Object cardData  = card.beforeUpdate(
                    loopDoc,
                    loopDoc.getData().get(defIV.getCardKey()),
                    existsOriginal ? original.getData().get(defIV.getCardKey()) : null,
                    defIV,
                    defIV.getConfig()
            );

            // 如果原始单据数据存在则更新，否则插入数据
            if(existsOriginal){

                DocI docI = BeanUtilz.copyFromObject(original.getItems().get(defIV.getCardKey()),DocI.class);
                docI.setCardContent(JSON.toJSONString(cardData));

                docIMapper.updateByPrimaryKeyWithBLOBs(docI);

                // 将更新后的数据放回到doc
                loopDoc.getItems().put(defIV.getCardKey(),docI);
                docI.setCardContent(null);
            }else{

                DocI docI = new DocI();
                docI.setDocId(loopDoc.getDocId());
                docI.setCardKey(defIV.getCardKey());
                docI.setCreatedTime(loopDoc.getUpdatedTime());
                docI.setUpdatedTime(loopDoc.getUpdatedTime());
                docI.setCardContent(JSON.toJSONString(cardData));

                docIMapper.insert(docI);

                // 将更新后的数据放回到doc
                loopDoc.getItems().put(defIV.getCardKey(),docI);
                docI.setCardContent(null);
            }

            loopDoc.getData().put(defIV.getCardKey(),cardData);
        });
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 保存卡片数据到数据库", doc.getDocId());

        if(original==null || !StringUtils.equals(loopDoc.getDocState(),original.getDocState())){
            // 单据状态发生变化
            docDefService.runLoopCards(loopDoc.getDef(),false, (card, defIV)->{
                Object cardData = loopDoc.getData().get(defIV.getCardKey());
                card.stateChanged(loopDoc, original, cardData, defIV, defIV.getConfig());
            });
            if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 触发卡片的状态变更事件", doc.getDocId());

            // 启动工作流
            if(CollectionUtils.isNotEmpty(loopDoc.getDef().getBpms())){
                loopDoc.getDef().getBpms()
                    .stream()
                    .filter(bpm->StringUtils.equals(loopDoc.getDocState(),bpm.getStartBy()))
                    .findFirst()
                    .ifPresent(bpm-> {
                        ProcessInstance instance = bpmTaskService.start(bpm.getProcessKey(), loopDoc.getDocId());
                        loopDoc.setProcessInstanceId(instance.getProcessInstanceId());
                        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 启动工作流 key = {}", doc.getDocId(), bpm.getProcessKey());
                    });
            }
        }

        // 卡片数据保存之后
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 准备触发卡片 afterUpdated 、updateCommitted 接口", doc.getDocId());
        docDefService.runLoopCards(loopDoc.getDef(),false, (card, defIV)->{

            boolean existsOriginal = existsOriginal(original, defIV);

            loopDoc.getData().put(
                    defIV.getCardKey(),
                    card.afterUpdated(
                            loopDoc,
                            loopDoc.getData().get(defIV.getCardKey()),
                            existsOriginal ? original.getData().get(defIV.getCardKey()) : null,
                            defIV,
                            defIV.getConfig()
                    ));

            // 调用 卡片 updateCommitted
            if(isOverride(card,"updateCommitted")){
                LocalSyncUtilz.runAfterCommit(()->
                        card.updateCommitted(
                                loopDoc,
                                loopDoc.getData().get(defIV.getCardKey()),
                                defIV,
                                defIV.getConfig()
                        )
                );
            }
        });
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 触发卡片 afterUpdated 、updateCommitted 接口完成", doc.getDocId());

        // 数据更新前后对比
        List<String> changedCard = new ArrayList<>();
        docDefService.runLoopCards(loopDoc.getDef(),false, (card, defIV)->{
            if(card.enableDataDiff()){
                if(optionalOriginal.isPresent()){

                    Object o1 =  loopDoc.getData().get(defIV.getCardKey());
                    Object o2 = original.getData().get(defIV.getCardKey());

                    if(!Objects.equals(JSONObject.toJSONString(o1),JSONObject.toJSONString(o2))){
                        changedCard.add(defIV.getCardKey());
                    }
                }else{
                    changedCard.add(defIV.getCardKey());
                }
            }
        });
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 对比修改内容 changedCard = {}", doc.getDocId(), changedCard);

        // 卡片更新完成
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 触发单据 afterUpdated 接口", doc.getDocId());
        processCycle(loopDoc, NkDocCycle.afterUpdated, (beanName)->{
            customObjectManager
                    .getCustomObject(beanName, NkDocUpdateInterceptor.class)
                    .apply(loopDoc, original, NkDocCycle.afterUpdated);
            return loopDoc;
        });

        loopDoc.setIdentification(random());
        if(optionalOriginal.isPresent()){
            docHMapper.updateByPrimaryKeySelective(loopDoc);
        }else{
            docHMapper.insert(loopDoc);
        }
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 保存单据抬头数据到数据库", doc.getDocId());
        docHistoryService.doAddVersion(loopDoc,original,changedCard,optSource);
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 增加单据修改历史记录", doc.getDocId());

        index(loopDoc);
        if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 创建重建index任务", doc.getDocId());

        LocalSyncUtilz.runAfterCommit(()->{
                processCycle(loopDoc, NkDocCycle.afterUpdateCommitted, (beanName)->{
                            customObjectManager
                                    .getCustomObject(beanName, NkDocCommittedInterceptor.class)
                                    .apply(loopDoc, NkDocCycle.afterUpdateCommitted);
                            return loopDoc;
                });
                if(log.isInfoEnabled())log.info("保存单据内容 docId = {}: 触发单据 afterUpdateCommitted 接口", doc.getDocId());
        });

        return loopDoc;
    }

    @Override
    public void doOnBpmKilled(DocHV docHV, String processKey, String optSource) {
        if(CollectionUtils.isNotEmpty(docHV.getDef().getBpms())){

            DocDefBpm docDefBpm = docHV.getDef().getBpms()
                    .stream().filter(i -> StringUtils.equals(i.getProcessKey(), processKey))
                    .findFirst()
                    .orElse(null);

            if(docDefBpm!=null){
                docHV.setDocState(docDefBpm.getRollbackTo());
                this.doUpdate(docHV,docHV,optSource);
            }
        }
    }

    private void index(DocHV doc){
        searchEngine.indexBeforeCommit(BeanUtilz.copyFromObject(doc, DocHES.class));
    }

    private boolean isOverride(Object obj, String methodName){
        try {
            if(AopUtils.isAopProxy(obj)){
                obj = ((Advised)obj).getTargetSource().getTarget();
            }

            assert obj != null;
            Method afterUpdated = obj.getClass().getDeclaredMethod(methodName, DocHV.class, Object.class, DocDefIV.class, Object.class);
            return afterUpdated.getDeclaringClass() != NkCard.class;
        } catch (Exception e) {

            return false;
        }
    }

    private DocHV processCycle(DocHV doc, NkDocCycle cycleName, Function<String, DocHV> function){

        List<DocDefCycle> collect =  doc.getDef().getLifeCycles()
                .stream()
                .filter(cycle -> StringUtils.equals(cycle.getDocCycle(), cycleName.name()))
                .collect(Collectors.toList());

        for(DocDefCycle cycle : collect){
            doc = function.apply(cycle.getRefObjectType());
        }

        return doc;
    }

    private String random(){
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+
                new DecimalFormat("00000").format(Math.random()*10000);
    }
}
