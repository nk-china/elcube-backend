package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.config.id.GUID;
import cn.nkpro.ts5.config.id.SequenceSupport;
import cn.nkpro.ts5.engine.LocalSyncUtilz;
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
import cn.nkpro.ts5.engine.elasticearch.model.CustomES;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.engine.spel.TfmsSpELManager;
import cn.nkpro.ts5.engine.task.NkBpmTaskService;
import cn.nkpro.ts5.exception.TfmsDefineException;
import cn.nkpro.ts5.orm.mb.gen.*;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
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
    @Autowired
    private DocIIndexMapper docIIndexMapper;
    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskService bpmTaskService;
    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private NkDocHistoryService docHistoryService;
    @Autowired
    private TfmsSpELManager spELManager;

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

        doc.setNewCreate(true);

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
    public final DocHBasis deserialize(DocDefHV def, DocHPersistent docHD){

        long start = System.currentTimeMillis();
        if(log.isInfoEnabled())
            log.info("{}反序列化卡片数据", NkDocEngineContext.currLog());

        DocHBasis doc = BeanUtilz.copyFromObject(docHD, DocHBasis.class);

        // 解析单据行项目数据
        docDefService.runLoopCards(def,false, (nkCard, defIV)->{

            // 获取行项目数据
            DocI docI = doc.getItems().computeIfAbsent(defIV.getCardKey(),(key)->{
                DocIV n = new DocIV();
                n.setCardKey(key);
                // warning: docId 作为保存时 insert 或 update 的判断，所以这里不要赋值
                // n.setDocId(doc.getDocId());
                return n;
            });

            if(log.isInfoEnabled())
                log.info("{}\tdeserialize cardKey = {} | {}, card = {}",
                        NkDocEngineContext.currLog(),
                        defIV.getCardKey(),
                        defIV.getCardName(),
                        nkCard.getBeanName()
                );
            doc.getData().put(
                    defIV.getCardKey(),
                    nkCard.deserialize(docI.getCardContent())
            );

        });

        if(log.isInfoEnabled())
            log.info("{}反序列化卡片数据 docId = {}: 耗时{}ms",
                    NkDocEngineContext.currLog(),
                    docHD.getDocId(),
                    System.currentTimeMillis() - start
            );

        return doc;
    }

    @Override
    public DocHV detail(DocDefHV def, DocHBasis docHD) {

        long start = System.currentTimeMillis();
        if(log.isInfoEnabled())log.info("{}获取单据内容", NkDocEngineContext.currLog());

        DocHV doc = BeanUtilz.copyFromObject(docHD, DocHV.class);
        doc.setDef(def);

        // afterGetData单据行项目数据
        docDefService.runLoopCards(def,false, (nkCard, defIV)-> {
            if(log.isInfoEnabled())
                log.info("{}\tafterGetData cardKey = {} | {}, card = {}",
                        NkDocEngineContext.currLog(),
                        defIV.getCardKey(),
                        defIV.getCardName(),
                        nkCard.getBeanName()
                );
            doc.getData().put(
                    defIV.getCardKey(),
                    nkCard.afterGetData(doc, doc.getData().get(defIV.getCardKey()), defIV, defIV.getConfig())
            );
        });

        humanize(doc);

        if(log.isInfoEnabled())
            log.info("{}获取单据内容 耗时{}ms",
                    NkDocEngineContext.currLog(),
                    System.currentTimeMillis() - start
            );

        return doc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocHV doUpdate(DocHV doc, DocHV original, String optSource) {

        if(log.isInfoEnabled())
            log.info("{}保存单据内容", NkDocEngineContext.currLog());

        // 原始单据
        Optional<DocHV> optionalOriginal = Optional.ofNullable(original);

        doc.setUpdatedTime(DateTimeUtilz.nowSeconds());

        if(!optionalOriginal.isPresent()){
            if(StringUtils.isBlank(doc.getDocNumber()))
                doc.setDocNumber(sequenceUtils.next(EnumDocClassify.valueOf(doc.getClassify()), doc.getDocType()));

            doc.setCreatedTime(doc.getUpdatedTime());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 开始处理单据卡片数据", NkDocEngineContext.currLog());
        docDefService.runLoopCards(doc.getDef(),false, (card, defIV)->
                doc.getData().put(
                        defIV.getCardKey(),
                        card.deserialize(doc.getData().get(defIV.getCardKey()))
                )
        );
        if(log.isInfoEnabled())log.info("{}保存单据内容 反序列化数据完成", NkDocEngineContext.currLog());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 触发单据 beforeUpdate 接口", NkDocEngineContext.currLog());
        DocHV loopDoc = processCycle(doc, NkDocCycle.beforeUpdate, (beanName)->
                customObjectManager
                        .getCustomObject(beanName, NkDocUpdateInterceptor.class)
                        .apply(doc, original, NkDocCycle.beforeUpdate)
        );

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 保存卡片数据到数据库", NkDocEngineContext.currLog());
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
            }

            loopDoc.getData().put(defIV.getCardKey(),cardData);
        });

        // 单据状态发生变化
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(original==null || !StringUtils.equals(loopDoc.getDocState(),original.getDocState())){
            if(log.isInfoEnabled())log.info("{}保存单据内容 触发卡片的状态变更事件", NkDocEngineContext.currLog());
            docDefService.runLoopCards(loopDoc.getDef(),false, (card, defIV)->{
                Object cardData = loopDoc.getData().get(defIV.getCardKey());
                card.stateChanged(loopDoc, original, cardData, defIV, defIV.getConfig());
            });

            // 启动工作流
            if(CollectionUtils.isNotEmpty(loopDoc.getDef().getBpms())){
                loopDoc.getDef().getBpms()
                    .stream()
                    .filter(bpm->StringUtils.equals(loopDoc.getDocState(),bpm.getStartBy()))
                    .findFirst()
                    .ifPresent(bpm-> {
                        if(log.isInfoEnabled())log.info("{}保存单据内容 启动工作流 key = {}", NkDocEngineContext.currLog(), bpm.getProcessKey());
                        ProcessInstance instance = bpmTaskService.start(bpm.getProcessKey(), loopDoc.getDocId());
                        loopDoc.setProcessInstanceId(instance.getProcessInstanceId());
                    });
            }
        }

        // 卡片数据保存
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 准备触发卡片 afterUpdated 、updateCommitted 接口", NkDocEngineContext.currLog());
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
            if(isOverride(card)){
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
        if(log.isInfoEnabled())log.info("{}保存单据内容 触发卡片 afterUpdated 、updateCommitted 接口完成", NkDocEngineContext.currLog());



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 计算动态索引字段", NkDocEngineContext.currLog());
        EvaluationContext context = spELManager.createContext(loopDoc);

        DocIIndexExample example = new DocIIndexExample();
        example.createCriteria().andDocIdEqualTo(loopDoc.getDocId());
        docIIndexMapper.deleteByExample(example);

        if(loopDoc.getDef().getIndexRules()!=null)
            loopDoc.getDef()
                .getIndexRules()
                .forEach(rule->{
                    String name = String.format("%s_%s",rule.getIndexName(),rule.getIndexType());
                    Object value = spELManager.invoke(rule.getRuleSpEL(),context);
                    String type = value==null?Void.class.getName():value.getClass().getName();
                    loopDoc.getDynamics().put(
                            name,
                            spELManager.invoke(rule.getRuleSpEL(),context)
                    );

                    DocIIndex index = new DocIIndex();
                    index.setDocId(loopDoc.getDocId());
                    index.setName(name);
                    index.setValue(JSON.toJSONString(value));
                    index.setDataType(type);
                    index.setOrderBy(rule.getOrderBy());
                    index.setUpdatedTime(DateTimeUtilz.nowSeconds());

                    if(optionalOriginal.isPresent()&&original.getDynamics().containsKey(name)){
                        docIIndexMapper.updateByPrimaryKey(index);
                    }else{
                        docIIndexMapper.insert(index);
                    }
                });
        // 删除已过时的索引
        if(optionalOriginal.isPresent()){
            original.getDynamics().forEach((k,v)->{
                if(!loopDoc.getDynamics().containsKey(k)){
                    DocIIndexKey key = new DocIIndexKey();
                    key.setDocId(loopDoc.getDocId());
                    key.setName(k);
                    docIIndexMapper.deleteByPrimaryKey(key);
                }
            });
        }

        // 业务主键
        loopDoc.setBusinessKey(StringUtils.EMPTY);
        if(StringUtils.isNotBlank(loopDoc.getDef().getBusinessKeySpEL())){
            Object businessKey = spELManager.invoke(loopDoc.getDef().getBusinessKeySpEL(), context);
            loopDoc.setBusinessKey(businessKey!=null?businessKey.toString():StringUtils.EMPTY);
            if(StringUtils.isNotBlank(loopDoc.getBusinessKey())&&
                    !(optionalOriginal.isPresent() && StringUtils.equals(loopDoc.getBusinessKey(),original.getBusinessKey()))){

                DocHExample docHExample = new DocHExample();
                docHExample.createCriteria()
                        .andBusinessKeyEqualTo(loopDoc.getBusinessKey());

                docHMapper.selectByExample(docHExample,new RowBounds(0,1))
                        .stream()
                        .filter(e->!StringUtils.equals(e.getDocId(),loopDoc.getDocId()))
                        .findFirst()
                        .ifPresent((e)->{throw new TfmsDefineException("业务主键重复");});
            }
        }

        // 数据更新前后对比
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 对比修改内容", NkDocEngineContext.currLog());
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
        if(log.isInfoEnabled())log.info("{}保存单据内容 对比修改内容完成 changedCard = {}", NkDocEngineContext.currLog(), changedCard);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 触发单据 afterUpdated 接口", NkDocEngineContext.currLog());
        processCycle(loopDoc, NkDocCycle.afterUpdated, (beanName)->{
            customObjectManager
                    .getCustomObject(beanName, NkDocUpdateInterceptor.class)
                    .apply(loopDoc, original, NkDocCycle.afterUpdated);
            return loopDoc;
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 保存单据抬头数据到数据库", NkDocEngineContext.currLog());

        loopDoc.setIdentification(random());
        if(optionalOriginal.isPresent()){
            docHMapper.updateByPrimaryKeySelective(loopDoc);
        }else{
            docHMapper.insert(loopDoc);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 增加单据修改历史记录", NkDocEngineContext.currLog());
        docHistoryService.doAddVersion(loopDoc,original,changedCard,optSource);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        humanize(doc);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        final String currLog = NkDocEngineContext.currLog();
        LocalSyncUtilz.runAfterCommit(()->{
            if(log.isInfoEnabled())log.info("{}保存单据内容 触发单据 afterUpdateCommitted 接口", currLog);
                processCycle(loopDoc, NkDocCycle.afterUpdateCommitted, (beanName)->{
                            customObjectManager
                                    .getCustomObject(beanName, NkDocCommittedInterceptor.class)
                                    .apply(loopDoc, NkDocCycle.afterUpdateCommitted);
                            return loopDoc;
                });
        });

        loopDoc.setNewCreate(false);




//        assert original != null;
//        List<Map<String,Object>> nkCardGrid1 = (List<Map<String,Object>>) loopDoc.getData().get("NkCardGrid");
//        List<Map<String,Object>> nkCardGrid2 = (List<Map<String,Object>>) original.getData().get("NkCardGrid");
//
//        diff(nkCardGrid1,nkCardGrid2,"KEY1",(key,e)->{
//            // add
//            CustomES customES = new CustomES();
//            customES.setItemId(key);
//            customES.setDocId(loopDoc.getDocId());
//            customES.getDynamics().putAll(e);
//
//            searchEngine.indexBeforeCommit(customES);
//        },(key,e)->{
//            // update
//            CustomES customES = new CustomES();
//            customES.setItemId(key);
//            customES.setDocId(loopDoc.getDocId());
//            customES.getDynamics().putAll(e);
//            searchEngine.updateBeforeCommit(customES);
//        },(key,e)->{
//            // delete
//            searchEngine.deleteBeforeCommit(CustomES.class, key);
//        });


        return loopDoc;
    }

    private <D> void diff(List<D> list1,List<D> list2,String keySpEL,DiffFunction<D> added,DiffFunction<D> updated,DiffFunction<D> removed){

        Map<String,D> map1 = list1.stream().collect(Collectors.toMap(e->(String)(spELManager.invoke(keySpEL,e)),e->e));
        Map<String,D> map2 = list2.stream().collect(Collectors.toMap(e->(String)(spELManager.invoke(keySpEL,e)),e->e));

        map1.forEach((k,v)->{
            if(map2.containsKey(k)){
                updated.apply(k,v);
            }else{
                added.apply(k,v);
            }
        });
        map2.forEach((k,v)->{
            if(!map1.containsKey(k)){
                removed.apply(k,v);
            }
        });
    }

    public interface DiffFunction<D>{
        public void apply(String key,D data);
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

    private boolean existsOriginal(DocHV original, DocDefIV defIV){
        return original !=null
                && original.getItems().containsKey(defIV.getCardKey())
                && StringUtils.isNotBlank(original.getItems().get(defIV.getCardKey()).getDocId());
    }

    private void humanize(DocHV doc){
        doc.setDocTypeDesc(String.format("%s | %s",doc.getDocType(),doc.getDef().getDocName()));
        doc.getDef().getStatus().stream()
                .filter(defDocStatus -> StringUtils.equals(defDocStatus.getDocState(),doc.getDocState()))
                .findAny().ifPresent(state ->
                doc.setDocStateDesc(String.format("%s | %s",state.getDocState(),state.getDocStateDesc()))
        );
    }

    private boolean isOverride(Object obj){
        try {
            if(AopUtils.isAopProxy(obj)){
                obj = ((Advised)obj).getTargetSource().getTarget();
            }

            assert obj != null;
            Method afterUpdated = obj.getClass().getDeclaredMethod("updateCommitted", DocHV.class, Object.class, DocDefIV.class, Object.class);
            return afterUpdated.getDeclaringClass() != NkCard.class;
        } catch (Exception e) {

            return false;
        }
    }

    private DocHV processCycle(DocHV doc, NkDocCycle cycleName, Function<String, DocHV> function){

        if(doc.getDef().getLifeCycles()!=null){
            List<DocDefCycle> collect =  doc.getDef().getLifeCycles()
                    .stream()
                    .filter(cycle -> StringUtils.equals(cycle.getDocCycle(), cycleName.name()))
                    .collect(Collectors.toList());

            for(DocDefCycle cycle : collect){
                doc = function.apply(cycle.getRefObjectType());
            }
        }

        return doc;
    }

    private String random(){
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+
                new DecimalFormat("00000").format(Math.random()*10000);
    }
}
