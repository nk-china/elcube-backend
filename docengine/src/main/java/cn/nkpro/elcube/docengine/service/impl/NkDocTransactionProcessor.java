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

import cn.nkpro.elcube.annotation.NkNote;
import cn.nkpro.elcube.basic.GUID;
import cn.nkpro.elcube.basic.TransactionSync;
import cn.nkpro.elcube.co.NkCustomObjectManager;
import cn.nkpro.elcube.co.spel.NkSpELManager;
import cn.nkpro.elcube.data.elasticearch.SearchEngine;
import cn.nkpro.elcube.docengine.*;
import cn.nkpro.elcube.docengine.gen.*;
import cn.nkpro.elcube.docengine.interceptor.NkDocCycleInterceptor;
import cn.nkpro.elcube.docengine.model.*;
import cn.nkpro.elcube.docengine.model.event.AbstractDocCycleEvent;
import cn.nkpro.elcube.docengine.model.event.DocCalculateEvent;
import cn.nkpro.elcube.docengine.model.event.DocCreateEvent;
import cn.nkpro.elcube.docengine.model.event.DocUpdateEvent;
import cn.nkpro.elcube.docengine.service.NkDocDefService;
import cn.nkpro.elcube.docengine.service.NkDocHistoryService;
import cn.nkpro.elcube.docengine.service.SequenceSupport;
import cn.nkpro.elcube.docengine.utils.RandomUtils;
import cn.nkpro.elcube.exception.NkDefineException;
import cn.nkpro.elcube.task.NkBpmTaskService;
import cn.nkpro.elcube.utils.BeanUtilz;
import cn.nkpro.elcube.utils.DateTimeUtilz;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Order(0)
@Slf4j
@NkNote("??????")
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
    @Lazy@Autowired@SuppressWarnings("all")
    private NkDocEngine docEngine;

    @Override
    public String desc() {
        return "Transaction | ??????";
    }

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.TRANSACTION;
    }

    @Override
    public DocHV toCreate(DocDefHV def, DocHV preDoc) {

        Optional<DocHV> optPreDoc = Optional.ofNullable(preDoc);

        DocHV doc = new DocHV();

        doc.setNewCreate(true);

        // ????????????
        doc.setDocId(guid.nextId(DocH.class));
        doc.setClassify(def.getDocClassify());
        doc.setDocType(def.getDocType());
        //doc.setDocName(def.getDocName()); ???????????????????????????????????????
        doc.setDocState(def.getStatus().get(0).getDocState());

        // ????????????
        doc.setRefObjectId(optPreDoc.map(DocHV::getRefObjectId).orElse(doc.getDocId()));
        doc.setPreDocId(optPreDoc.map(DocHV::getDocId).orElse("@"));

        // ????????????
        doc.setCreatedTime(DateTimeUtilz.nowSeconds());
        doc.setDefVersion(def.getVersion());

        // ????????????
        //doc.setPreDoc(preDoc);
        doc.setDef(def);

        AtomicReference<DocHV> atomic = new AtomicReference(doc);

        atomic.set(processCycle(atomic.get(), DocCreateEvent
                .build(NkDocCycle.beforeCreate, preDoc)));

        docDefService.runLoopCards(atomic.get().getDocId(), def,true,false, (card, defIV)->
                atomic.get().getData().put(
                defIV.getCardKey(),
                card.afterCreate(atomic.get(),preDoc,card.deserialize(null), defIV, defIV.getConfig())
            )
        );

        return processCycle(atomic.get(), DocCreateEvent.build(NkDocCycle.afterCreated, preDoc));
    }

    @Override
    public Object call(DocHV doc, String fromCard, String method, Object options) {

        docDefService.runLoopCards(doc.getDocId(), doc.getDef(),false,false, (card, defIV)->
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
    public DocHV calculate(DocHV doc, String fromCard, Object options){

        AtomicReference<DocHV> atomic = new AtomicReference(doc);

        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false,false, (card, defIV)->
                atomic.get().getData().put(
                    defIV.getCardKey(),
                    card.deserialize(atomic.get().getData().get(defIV.getCardKey()))
            )
        );

        atomic.set(processCycle(atomic.get(), DocCalculateEvent.build(NkDocCycle.beforeCalculate)));

        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),true,false, (card, defIV)->{

            // ??????????????????????????????
            boolean isTrigger = StringUtils.equals(fromCard,defIV.getCardKey());

            // ????????????????????????????????????
            Object itemOptions = isTrigger?options:null;

            Object cardData = atomic.get().getData().get(defIV.getCardKey());

            atomic.get().getData().put(
                    defIV.getCardKey(),
                    card.calculate(atomic.get(), cardData, defIV, defIV.getConfig(), isTrigger, itemOptions)
            );
        });

        return processCycle(atomic.get(), DocCalculateEvent.build(NkDocCycle.afterCalculated));
    }

    @Override
    public final DocHBasis deserialize(DocDefHV def, DocHPersistent docHD){

        long start = System.currentTimeMillis();
        if(log.isInfoEnabled())
            log.info("????????????????????????");

        DocHBasis doc = BeanUtilz.copyFromObject(docHD, DocHBasis.class);

        // ???????????????????????????
        docDefService.runLoopCards(doc.getDocId(), def,false,false, (nkCard, defIV)->{

            // ?????????????????????
            DocI docI = doc.getItems().computeIfAbsent(defIV.getCardKey(),(key)->{
                DocIV n = new DocIV();
                n.setCardKey(key);
                // warning: docId ??????????????? insert ??? update ????????????????????????????????????
                // n.setDocId(doc.getDocId());
                return n;
            });

            if(log.isInfoEnabled())
                log.info("\tdeserialize cardKey = {} | {}, card = {}",
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
            log.info("???????????????????????? docId = {}: ??????{}ms",
                    docHD.getDocId(),
                    System.currentTimeMillis() - start
            );

        return doc;
    }

    @Override
    public DocHV detail(DocDefHV def, DocHBasis docHD) {

        long start = System.currentTimeMillis();
        if(log.isInfoEnabled())log.info("??????????????????");

        DocHV doc = BeanUtilz.copyFromObject(docHD, DocHV.class);
        doc.setDef(def);

        // afterGetData?????????????????????
        docDefService.runLoopCards(doc.getDocId(), def,false,false, (nkCard, defIV)-> {
            if(log.isInfoEnabled())
                log.info("\tafterGetData cardKey = {} | {}, card = {}",
                        defIV.getCardKey(),
                        defIV.getCardName(),
                        nkCard.getBeanName()
                );
            doc.getData().put(
                    defIV.getCardKey(),
                    nkCard.afterGetData(doc, doc.getData().get(defIV.getCardKey()), defIV, defIV.getConfig())
            );
        });

        fixPartnerName(doc);
        humanize(doc);

        if(log.isInfoEnabled())
            log.info("?????????????????? ??????{}ms",
                    System.currentTimeMillis() - start
            );

        return doc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocHV doUpdate(DocHV doc, DocHV original, String optSource) {

        if(log.isInfoEnabled())
            log.info("??????????????????");

        AtomicReference<DocHV> atomic = new AtomicReference(doc);

        // ????????????
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
        if(log.isInfoEnabled())log.info("?????????????????? ??????????????????????????????");
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false,false, (card, defIV)->
                atomic.get().getData().put(
                        defIV.getCardKey(),
                        card.deserialize(atomic.get().getData().get(defIV.getCardKey()))
                )
        );
        if(log.isInfoEnabled())log.info("?????????????????? ????????????????????????");

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("?????????????????? ???????????? beforeUpdate ??????");
        atomic.set(processCycle(doc, DocUpdateEvent.build(NkDocCycle.beforeUpdate, original)));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("?????????????????? ??????????????????????????????");
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false,false, (card, defIV)->{

            boolean existsOriginal = existsOriginal(original, defIV);

            Object cardData  = card.beforeUpdate(
                    atomic.get(),
                    atomic.get().getData().get(defIV.getCardKey()),
                    existsOriginal ? optionalOriginal.get().getData().get(defIV.getCardKey()) : null,
                    defIV,
                    defIV.getConfig()
            );

            // ????????????????????????????????????????????????????????????
            if(existsOriginal){

                DocI docI = BeanUtilz.copyFromObject(optionalOriginal.get().getItems().get(defIV.getCardKey()),DocI.class);
                docI.setCardContent(cardData!=null?JSON.toJSONString(cardData):null);

                docIMapper.updateByPrimaryKeyWithBLOBs(docI);

                // ??????????????????????????????doc
                atomic.get().getItems().put(defIV.getCardKey(),docI);
            }else{

                DocI docI = new DocI();
                docI.setDocId(atomic.get().getDocId());
                docI.setCardKey(defIV.getCardKey());
                docI.setCreatedTime(atomic.get().getUpdatedTime());
                docI.setUpdatedTime(atomic.get().getUpdatedTime());
                docI.setCardContent(JSON.toJSONString(cardData));

                docIMapper.insert(docI);

                // ??????????????????????????????doc
                atomic.get().getItems().put(defIV.getCardKey(),docI);
            }

            // atomic.get().getData().put(defIV.getCardKey(),cardData);
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // ??????????????????????????????????????????????????????
        fixPartnerName(doc);
        humanize(doc);

        // ????????????????????????
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(original==null || !StringUtils.equals(atomic.get().getDocState(),original.getDocState())){
            if(log.isInfoEnabled())log.info("?????????????????? ?????????????????????????????????");
            docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false,false, (card, defIV)->{
                Object cardData = atomic.get().getData().get(defIV.getCardKey());
                card.stateChanged(atomic.get(), original, cardData, defIV, defIV.getConfig());
            });

            // ???????????????
            if(CollectionUtils.isNotEmpty(atomic.get().getDef().getBpms())){
                atomic.get().getDef().getBpms()
                    .stream()
                    .filter(bpm->StringUtils.equals(atomic.get().getDocState(),bpm.getStartBy()))
                    .findFirst()
                    .ifPresent(bpm-> {
                        if(log.isInfoEnabled())log.info("?????????????????? ??????????????? key = {}", bpm.getProcessKey());

                        try{
                            NkDocEngineThreadLocal.setCurr(atomic.get());
                            String processInstanceId = bpmTaskService.start(bpm.getProcessKey(), atomic.get().getDocId());
                            atomic.get().setProcessInstanceId(processInstanceId);

                            // ??????????????????????????????????????????????????????????????????????????????
                            humanize(atomic.get());
                        }finally {
                            NkDocEngineThreadLocal.clearCurr();
                        }
                    });
            }
        }

        // ??????????????????
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("?????????????????? ?????????????????? afterUpdated ???updateCommitted ??????");
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false,false, (card, defIV)->{

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

            // ?????? ?????? updateCommitted
            if(isOverride(card)){
                TransactionSync.runAfterCommit("????????????updateCommitted",()->
                        card.updateCommitted(
                                atomic.get(),
                                atomic.get().getData().get(defIV.getCardKey()),
                                defIV,
                                defIV.getConfig()
                        )
                );
            }
        });
        if(log.isInfoEnabled())log.info("?????????????????? ???????????? afterUpdated ???updateCommitted ????????????");



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("?????????????????? ????????????????????????");


        doUpdateIndex(atomic.get(), optionalOriginal.orElse(null), false);

        // ????????????????????????
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("?????????????????? ??????????????????");
        List<String> changedCard = new ArrayList<>();
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false,false, (card, defIV)->{
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
        if(log.isInfoEnabled())log.info("?????????????????? ???????????????????????? changedCard = {}", changedCard);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        NkDocEngineThreadLocal.addUpdated(doc);
        if(log.isInfoEnabled())log.info("?????????????????? ???????????? afterUpdated ??????");
        processCycle(atomic.get(), DocUpdateEvent.build(NkDocCycle.afterUpdated,original));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("?????????????????? ????????????????????????????????????");

        atomic.get().setIdentification(random());
        if(optionalOriginal.isPresent()){
            docHMapper.updateByPrimaryKeySelective(atomic.get());
        }else{
            docHMapper.insert(atomic.get());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(log.isInfoEnabled())log.info("?????????????????? ??????????????????????????????");
        docHistoryService.doAddVersion(atomic.get(),original,changedCard,optSource);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        TransactionSync.runAfterCommit("????????????afterUpdateCommitted",()->{
            if(log.isInfoEnabled())log.info("?????????????????? ???????????? afterUpdateCommitted ??????");
            processCycle(atomic.get(), DocUpdateEvent.build(NkDocCycle.afterUpdateCommitted,original));
        });

        atomic.get().setNewCreate(false);

        return atomic.get();
    }

    /**
     *
     * ???doc ??? original?????????????????????original???null ?????? ?????????????????????????????????????????????????????????????????????
     *
     * @param doc ??????
     * @param original ????????????
     * @param forceUpdateDocHV ???????????????????????? update doc_h ???
     */
    @Override
    public void doUpdateIndex(DocHV doc, DocHV original, boolean forceUpdateDocHV){

        EvaluationContext context = spELManager.createContext(doc);

        doc.getDynamics().clear();

        DocIIndexExample example = new DocIIndexExample();
        example.createCriteria().andDocIdEqualTo(doc.getDocId());
        docIIndexMapper.deleteByExample(example);

        if(doc.getDef().getIndexRules()!=null)
            doc.getDef()
                    .getIndexRules()
                    .forEach(rule->{
                        String name = String.format("%s_%s",rule.getIndexName(),rule.getIndexType());
                        Object value = spELManager.invoke(rule.getRuleSpEL(),context);

                        if(value!=null){

                            doc.getDynamics().put(name,value);

                            List<Object> list = new ArrayList<>();
                            if(value instanceof List){
                                list.addAll((List) value);
                            }else if(value.getClass().isArray()){
                                list.addAll(Arrays.asList((Object[])value));
                            }else{
                                list.add(value);
                            }
                            list.forEach(item->{

                                DocIIndex index = new DocIIndex();
                                index.setDocId(doc.getDocId());
                                index.setName(name);
                                index.setSeq(list.indexOf(item));
                                index.setOrderBy(doc.getDynamics().size()+rule.getOrderBy());
                                index.setUpdatedTime(DateTimeUtilz.nowSeconds());

                                if(item == null){
                                    index.setDataType(Void.class.getName());
                                    index.setValue(null);
                                }else
                                if(item instanceof Number){
                                    index.setDataType(Number.class.getName());
                                    index.setNumberValue(((Number) item).doubleValue());
                                }else
                                if(item instanceof String){
                                    index.setDataType(item.getClass().getName());
                                    index.setValue(item.toString());
                                }

                                docIIndexMapper.insert(index);

                            });
                        }
                    });



        // ????????????
        doc.setBusinessKey(StringUtils.EMPTY);
        if(StringUtils.isNotBlank(doc.getDef().getBusinessKeySpEL())){
            Object businessKey = spELManager.invoke(doc.getDef().getBusinessKeySpEL(), context);
            doc.setBusinessKey(businessKey!=null?businessKey.toString():StringUtils.EMPTY);

            // ????????????????????????????????????????????????????????????????????????
            if(
                    StringUtils.isNotBlank(doc.getBusinessKey())&&
                            !(
                                    original!=null &&
                                            StringUtils.equals(doc.getBusinessKey(),original.getBusinessKey())
                            )
                    ){

                DocHExample docHExample = new DocHExample();
                docHExample.createCriteria()
                        .andBusinessKeyEqualTo(doc.getBusinessKey());

                docHMapper.selectByExample(docHExample,new RowBounds(0,1))
                        .stream()
                        .filter(e->!StringUtils.equals(e.getDocId(),doc.getDocId()))
                        .findFirst()
                        .ifPresent((e)->{throw new NkDefineException("??????????????????");});
            }

            if(forceUpdateDocHV){
                DocH record = new DocH();
                record.setDocId(doc.getDocId());
                record.setBusinessKey(doc.getBusinessKey());
                docHMapper.updateByPrimaryKeySelective(record);
            }
        }
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
        docDefService.runLoopCards(atomic.get().getDocId(), atomic.get().getDef(),false,false, (card, defIV)->
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


    private void fixPartnerName(DocHV doc){

        // ????????????????????????
        if(StringUtils.isNotBlank(doc.getPartnerId())){
            if(StringUtils.equals(doc.getDocId(),doc.getPartnerId())){
                doc.setPartnerName(doc.getDocName());
            }else{
                if(NkDocEngineThreadLocal.existUpdated(doc.getPartnerId())){
                    doc.setPartnerName(
                            NkDocEngineThreadLocal.getUpdated(doc.getPartnerId())
                                    .getDocName()
                    );
                }else{
                    doc.setPartnerName(
                            docEngine.detail(doc.getPartnerId())
                                    .getDocName()
                    );
                }
            }
        }
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

    private DocHV processCycle(DocHV doc, AbstractDocCycleEvent context){

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
