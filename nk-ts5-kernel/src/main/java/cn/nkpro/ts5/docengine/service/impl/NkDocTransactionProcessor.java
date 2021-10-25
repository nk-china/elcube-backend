package cn.nkpro.ts5.docengine.service.impl;

import cn.nkpro.ts5.basic.GUID;
import cn.nkpro.ts5.basic.TransactionSync;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.data.elasticearch.SearchEngine;
import cn.nkpro.ts5.docengine.NkCard;
import cn.nkpro.ts5.docengine.NkDocCycle;
import cn.nkpro.ts5.docengine.NkDocProcessor;
import cn.nkpro.ts5.docengine.gen.*;
import cn.nkpro.ts5.docengine.interceptor.NkDocCycleInterceptor;
import cn.nkpro.ts5.docengine.model.*;
import cn.nkpro.ts5.docengine.service.*;
import cn.nkpro.ts5.docengine.utils.RandomUtils;
import cn.nkpro.ts5.exception.NkDefineException;
import cn.nkpro.ts5.spel.NkSpELManager;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.apifan.common.random.source.PersonInfoSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    private DocIIndexMapper docIIndexMapper;
    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskService bpmTaskService;
    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private NkDocHistoryService docHistoryService;
    @Autowired@SuppressWarnings("all")
    private NkSpELManager spELManager;

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

        AtomicReference<DocHV> atomic = new AtomicReference(doc);

        NkDocCycleContext context = NkDocCycleContext
                .build(NkDocCycle.beforeCreate)
                .prev(preDoc);
        atomic.set(processCycle(atomic.get(), context));

        docDefService.runLoopCards(atomic.get().getDocId(), def,false, (card, defIV)->
                atomic.get().getData().put(
                defIV.getCardKey(),
                card.afterCreate(atomic.get(),preDoc,card.deserialize(null), defIV, defIV.getConfig())
            )
        );

        return processCycle(atomic.get(), context.cycle(NkDocCycle.afterCreated));
    }

    @Override
    public Object call(DocHV doc, String fromCard, String method, String options) {

        docDefService.runLoopCards(doc.getDocId(), doc.getDef(),false, (card, defIV)->
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

        AtomicReference<DocHV> atomic = new AtomicReference(doc);

        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false, (card, defIV)->
                atomic.get().getData().put(
                    defIV.getCardKey(),
                    card.deserialize(atomic.get().getData().get(defIV.getCardKey()))
            )
        );

        atomic.set(processCycle(atomic.get(), NkDocCycleContext.build(NkDocCycle.beforeCalculate)));

        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false, (card, defIV)->{

            // 是否为触发计算的卡片
            boolean isTrigger = StringUtils.equals(fromCard,defIV.getCardKey());

            // 获取计算函数的自定义选项
            String itemOptions = isTrigger?options:null;

            Object cardData = atomic.get().getData().get(defIV.getCardKey());

            atomic.get().getData().put(
                    defIV.getCardKey(),
                    card.calculate(atomic.get(), cardData, defIV, defIV.getConfig(), isTrigger, itemOptions)
            );
        });

        return processCycle(atomic.get(), NkDocCycleContext.build(NkDocCycle.afterCalculated));
    }

    @Override
    public final DocHBasis deserialize(DocDefHV def, DocHPersistent docHD){

        long start = System.currentTimeMillis();
        if(log.isInfoEnabled())
            log.info("{}反序列化卡片数据", NkDocEngineContext.currLog());

        DocHBasis doc = BeanUtilz.copyFromObject(docHD, DocHBasis.class);

        // 解析单据行项目数据
        docDefService.runLoopCards(doc.getDocId(), def,false, (nkCard, defIV)->{

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
        docDefService.runLoopCards(doc.getDocId(), def,false, (nkCard, defIV)-> {
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

        AtomicReference<DocHV> atomic = new AtomicReference(doc);
        // 原始单据
        Optional<DocHV> optionalOriginal = Optional.ofNullable(original);

        atomic.get().setUpdatedTime(DateTimeUtilz.nowSeconds());

        if(!optionalOriginal.isPresent()){
            if(StringUtils.isBlank(atomic.get().getDocNumber()))
                atomic.get().setDocNumber(sequenceUtils.next(EnumDocClassify.valueOf(atomic.get().getClassify()), atomic.get().getDocType()));

            atomic.get().setCreatedTime(atomic.get().getUpdatedTime());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 开始处理单据卡片数据", NkDocEngineContext.currLog());
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false, (card, defIV)->
                atomic.get().getData().put(
                        defIV.getCardKey(),
                        card.deserialize(atomic.get().getData().get(defIV.getCardKey()))
                )
        );
        if(log.isInfoEnabled())log.info("{}保存单据内容 反序列化数据完成", NkDocEngineContext.currLog());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 触发单据 beforeUpdate 接口", NkDocEngineContext.currLog());
        atomic.set(processCycle(doc, NkDocCycleContext.build(NkDocCycle.beforeUpdate)));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 保存卡片数据到数据库", NkDocEngineContext.currLog());
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false, (card, defIV)->{

            boolean existsOriginal = existsOriginal(original, defIV);

            Object cardData  = card.beforeUpdate(
                    atomic.get(),
                    atomic.get().getData().get(defIV.getCardKey()),
                    existsOriginal ? optionalOriginal.get().getData().get(defIV.getCardKey()) : null,
                    defIV,
                    defIV.getConfig()
            );

            // 如果原始单据数据存在则更新，否则插入数据
            if(existsOriginal){

                DocI docI = BeanUtilz.copyFromObject(optionalOriginal.get().getItems().get(defIV.getCardKey()),DocI.class);
                docI.setCardContent(JSON.toJSONString(cardData));

                docIMapper.updateByPrimaryKeyWithBLOBs(docI);

                // 将更新后的数据放回到doc
                atomic.get().getItems().put(defIV.getCardKey(),docI);
            }else{

                DocI docI = new DocI();
                docI.setDocId(atomic.get().getDocId());
                docI.setCardKey(defIV.getCardKey());
                docI.setCreatedTime(atomic.get().getUpdatedTime());
                docI.setUpdatedTime(atomic.get().getUpdatedTime());
                docI.setCardContent(JSON.toJSONString(cardData));

                docIMapper.insert(docI);

                // 将更新后的数据放回到doc
                atomic.get().getItems().put(defIV.getCardKey(),docI);
            }

            atomic.get().getData().put(defIV.getCardKey(),cardData);
        });

        // 单据状态发生变化
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(original==null || !StringUtils.equals(atomic.get().getDocState(),original.getDocState())){
            if(log.isInfoEnabled())log.info("{}保存单据内容 触发卡片的状态变更事件", NkDocEngineContext.currLog());
            docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false, (card, defIV)->{
                Object cardData = atomic.get().getData().get(defIV.getCardKey());
                card.stateChanged(atomic.get(), original, cardData, defIV, defIV.getConfig());
            });

            // 启动工作流
            if(CollectionUtils.isNotEmpty(atomic.get().getDef().getBpms())){
                atomic.get().getDef().getBpms()
                    .stream()
                    .filter(bpm->StringUtils.equals(atomic.get().getDocState(),bpm.getStartBy()))
                    .findFirst()
                    .ifPresent(bpm-> {
                        if(log.isInfoEnabled())log.info("{}保存单据内容 启动工作流 key = {}", NkDocEngineContext.currLog(), bpm.getProcessKey());
                        atomic.get().setProcessInstanceId(bpmTaskService.start(bpm.getProcessKey(), atomic.get().getDocId()));
                    });
            }
        }

        // 卡片数据保存
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 准备触发卡片 afterUpdated 、updateCommitted 接口", NkDocEngineContext.currLog());
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false, (card, defIV)->{

            boolean existsOriginal = existsOriginal(original, defIV);

            atomic.get().getData().put(
                    defIV.getCardKey(),
                    card.afterUpdated(
                            atomic.get(),
                            atomic.get().getData().get(defIV.getCardKey()),
                            existsOriginal ? original.getData().get(defIV.getCardKey()) : null,
                            defIV,
                            defIV.getConfig()
                    ));

            // 调用 卡片 updateCommitted
            if(isOverride(card)){
                TransactionSync.runAfterCommit(()->
                        card.updateCommitted(
                                atomic.get(),
                                atomic.get().getData().get(defIV.getCardKey()),
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
        EvaluationContext context = spELManager.createContext(atomic.get());

        //DocIIndexExample example = new DocIIndexExample();
        //example.createCriteria().andDocIdEqualTo(atomic.get().getDocId());
        //docIIndexMapper.deleteByExample(example);

        if(atomic.get().getDef().getIndexRules()!=null)
            atomic.get().getDef()
                .getIndexRules()
                .forEach(rule->{
                    String name = String.format("%s_%s",rule.getIndexName(),rule.getIndexType());
                    Object value = spELManager.invoke(rule.getRuleSpEL(),context);
                    String type = value==null?Void.class.getName():value.getClass().getName();
                    atomic.get().getDynamics().put(
                            name,
                            spELManager.invoke(rule.getRuleSpEL(),context)
                    );

                    DocIIndex index = new DocIIndex();
                    index.setDocId(atomic.get().getDocId());
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
        optionalOriginal.ifPresent(o -> o.getDynamics().forEach((k, v) -> {
            if (!atomic.get().getDynamics().containsKey(k)) {
                DocIIndexKey key = new DocIIndexKey();
                key.setDocId(atomic.get().getDocId());
                key.setName(k);
                docIIndexMapper.deleteByPrimaryKey(key);
            }
        }));

        // 业务主键
        atomic.get().setBusinessKey(StringUtils.EMPTY);
        if(StringUtils.isNotBlank(atomic.get().getDef().getBusinessKeySpEL())){
            Object businessKey = spELManager.invoke(atomic.get().getDef().getBusinessKeySpEL(), context);
            atomic.get().setBusinessKey(businessKey!=null?businessKey.toString():StringUtils.EMPTY);
            if(
                    StringUtils.isNotBlank(atomic.get().getBusinessKey())&&
                    !(
                            optionalOriginal.isPresent() &&
                                    StringUtils.equals(atomic.get().getBusinessKey(),optionalOriginal.get().getBusinessKey())
                    )
            ){

                DocHExample docHExample = new DocHExample();
                docHExample.createCriteria()
                        .andBusinessKeyEqualTo(atomic.get().getBusinessKey());

                docHMapper.selectByExample(docHExample,new RowBounds(0,1))
                        .stream()
                        .filter(e->!StringUtils.equals(e.getDocId(),atomic.get().getDocId()))
                        .findFirst()
                        .ifPresent((e)->{throw new NkDefineException("业务主键重复");});
            }
        }

        // 数据更新前后对比
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 对比修改内容", NkDocEngineContext.currLog());
        List<String> changedCard = new ArrayList<>();
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false, (card, defIV)->{
            if(card.enableDataDiff()){
                if(optionalOriginal.isPresent()){

                    Object o1 =  atomic.get().getData().get(defIV.getCardKey());
                    Object o2 =      original.getData().get(defIV.getCardKey());

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
        processCycle(atomic.get(), NkDocCycleContext.build(NkDocCycle.afterCalculated).original(original));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 保存单据抬头数据到数据库", NkDocEngineContext.currLog());

        atomic.get().setIdentification(random());
        if(optionalOriginal.isPresent()){
            docHMapper.updateByPrimaryKeySelective(atomic.get());
        }else{
            docHMapper.insert(atomic.get());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("{}保存单据内容 增加单据修改历史记录", NkDocEngineContext.currLog());
        docHistoryService.doAddVersion(atomic.get(),original,changedCard,optSource);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        humanize(doc);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        final String currLog = NkDocEngineContext.currLog();
        TransactionSync.runAfterCommit(()->{
            if(log.isInfoEnabled())log.info("{}保存单据内容 触发单据 afterUpdateCommitted 接口", currLog);
            processCycle(atomic.get(), NkDocCycleContext.build(NkDocCycle.afterUpdateCommitted));
        });

        atomic.get().setNewCreate(false);

        return atomic.get();
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

    @Override
    public DocHV random(DocHV doc) {

        doc.setDocName(RandomUtils.randomText());
        doc.setDocDesc(RandomUtils.randomText());
        doc.setPartnerName(PersonInfoSource.getInstance().randomChineseName());

        AtomicReference<DocHV> atomic = new AtomicReference(doc);
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false, (card, defIV)->
            atomic.get().getData().put(
                    defIV.getCardKey(),
                    card.random(atomic.get(), defIV, defIV.getConfig())
            )
        );

        return atomic.get();
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

    private DocHV processCycle(DocHV doc, NkDocCycleContext context){

        if(doc.getDef().getLifeCycles()!=null){
            doc.getDef().getLifeCycles()
                    .stream()
                    .filter(cycle -> StringUtils.equals(cycle.getDocCycle(), context.getCycle().name()))
                    .forEach(cycle ->
                        customObjectManager
                            .getCustomObject(cycle.getRefObjectType(), NkDocCycleInterceptor.class)
                            .apply(doc,context)
                    );
        }

        return doc;
    }

    private String random(){
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+
                new DecimalFormat("00000").format(Math.random()*10000);
    }
}
