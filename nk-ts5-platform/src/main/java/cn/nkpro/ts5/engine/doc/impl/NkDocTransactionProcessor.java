package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.config.id.GUID;
import cn.nkpro.ts5.config.id.SequenceSupport;
import cn.nkpro.ts5.engine.doc.NkCard;
import cn.nkpro.ts5.engine.doc.NkDocProcessor;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHD;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.model.DocIV;
import cn.nkpro.ts5.engine.doc.service.NkDocDefService;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.orm.mb.gen.DocH;
import cn.nkpro.ts5.orm.mb.gen.DocHMapper;
import cn.nkpro.ts5.orm.mb.gen.DocI;
import cn.nkpro.ts5.orm.mb.gen.DocIMapper;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.ts5.utils.LocalSyncUtilz;
import cn.nkpro.ts5.utils.VersioningUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;

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

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.TRANSACTION;
    }
    @Override
    public String desc() {
        return "交易";
    }

    @Override
    public DocHV toCreate(DocDefHV def, DocHV preDoc) throws Exception {

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

        docDefService.runLoopCards(def,false, (card, defIV)->
            doc.getData().put(
                defIV.getCardKey(),
                card.afterCreate(doc,preDoc,card.deserialize(null),defIV.getConfig())
            )
        );

        return doc;
    }

    @Override
    public DocHV calculate(DocHV doc, String fromCard, String options) throws Exception {

        docDefService.runLoopCards(doc.getDef(),false, (card, defIV)->
            doc.getData().put(
                    defIV.getCardKey(),
                    card.deserialize(doc.getData().get(defIV.getCardKey()))
            )
        );

        docDefService.runLoopCards(doc.getDef(),false, (card, defIV)->{

            // 是否为触发计算的卡片
            boolean isTrigger = StringUtils.equals(fromCard,defIV.getCardKey());

            // 获取计算函数的自定义选项
            String itemOptions = isTrigger?options:null;

            Object cardData = doc.getData().get(defIV.getCardKey());

            doc.getData().put(
                    defIV.getCardKey(),
                    card.calculate(doc, cardData, defIV.getConfig(), isTrigger, itemOptions)
            );
        });
        return doc;
    }

    @Override
    public DocHV detail(DocDefHV def, DocHD docHD) throws Exception {

        DocHV doc = BeanUtilz.copyFromObject(docHD, DocHV.class);
        doc.setDef(def);

        // 解析单据行项目数据
        docDefService.runLoopCards(def,false, (nkCard, docDefIV)->{

            // 获取行项目数据
            DocI docI = doc.getItems().computeIfAbsent(docDefIV.getCardKey(),(key)->{
                DocIV n = new DocIV();
                n.setCardKey(key);
                // warning: docId 作为保存时 insert 或 update 的判断，所以这里不要赋值
                // n.setDocId(doc.getDocId());
                return n;
            });

            doc.getData().put(
                docDefIV.getCardKey(),
                nkCard.afterGetData(doc, nkCard.deserialize(docI.getCardContent()), docDefIV.getConfig())
            );

            // 清除JSON，避免无效数据网络传输
            docI.setCardContent(null);
        });

        return doc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocHV doUpdate(DocDefHV def, DocHV doc, DocHV original, String optSource) throws Exception {
        // 原始单据
        Optional<DocHV> optionalOriginal = Optional.ofNullable(original);

        doc.setUpdatedTime(DateTimeUtilz.nowSeconds());

        if(!optionalOriginal.isPresent()){
            if(StringUtils.isBlank(doc.getDocNumber()))
                doc.setDocNumber(sequenceUtils.next(EnumDocClassify.valueOf(doc.getClassify()), doc.getDocType()));

            doc.setCreatedTime(doc.getUpdatedTime());
        }

        // todo 检查单据状态是否合法，即符合单据配置中的 状态流

        docDefService.runLoopCards(doc.getDef(),false, (card, defIV)->
                doc.getData().put(
                        defIV.getCardKey(),
                        card.deserialize(doc.getData().get(defIV.getCardKey()))
                )
        );

        docDefService.runLoopCards(def,false, (card, defIV)->{

            boolean existsOriginal = original !=null
                    && original.getItems().containsKey(defIV.getCardKey())
                    && StringUtils.isNotBlank(original.getItems().get(defIV.getCardKey()).getDocId());
            Object cardDataOriginal = null;
            Object cardData = doc.getData().get(defIV.getCardKey());

            if(existsOriginal){
                cardDataOriginal = original.getData().get(defIV.getCardKey());
            }

            cardData = card.beforeUpdate(doc, cardData, defIV.getConfig(), cardDataOriginal);

            // 如果原始单据数据存在则更新，否则插入数据
            if(existsOriginal){

                DocI docI = BeanUtilz.copyFromObject(original.getItems().get(defIV.getCardKey()),DocI.class);
                docI.setCardContent(JSON.toJSONString(cardData));

                docIMapper.updateByPrimaryKeyWithBLOBs(docI);

                // 将更新后的数据放回到doc
                doc.getItems().put(defIV.getCardKey(),docI);
                docI.setCardContent(null);
            }else{

                DocI docI = new DocI();
                docI.setDocId(doc.getDocId());
                docI.setCardKey(defIV.getCardKey());
                docI.setCreatedTime(doc.getUpdatedTime());
                docI.setUpdatedTime(doc.getUpdatedTime());
                docI.setCardContent(JSON.toJSONString(cardData));

                docIMapper.insert(docI);

                // 将更新后的数据放回到doc
                doc.getItems().put(defIV.getCardKey(),docI);
                docI.setCardContent(null);
            }

            doc.getData().put(defIV.getCardKey(),cardData);

            // 调用 afterUpdated
            if(isOverride(card)){
                Object loopCardData = cardData;
                LocalSyncUtilz.runAfterCommit(()-> card.afterUpdated(doc, loopCardData, defIV.getConfig()));
            }
        });

        if(optionalOriginal.isPresent()){
            docHMapper.updateByPrimaryKeySelective(doc);
        }else{
            docHMapper.insert(doc);
        }

        index(doc);

        return doc;
    }

    private void index(DocHV doc){

        DocHES docHES = BeanUtilz.copyFromObject(doc, DocHES.class);

        //docHES.setTags(StringUtils.split(docBO.getDocTags(),','));
        docHES.setDocTypeDesc(String.format("%s | %s",doc.getDocType(),doc.getDef().getDocName()));
        doc.getDef().getStatus().stream()
                .filter(defDocStatus -> StringUtils.equals(defDocStatus.getDocState(),doc.getDocState()))
                .findAny()
                .ifPresent(state ->
                        docHES.setDocStateDesc(
                                String.format("%s | %s",state.getDocState(),state.getDocStateDesc())
                        )
                );

        searchEngine.indexBeforeCommit(docHES);
    }

    private boolean isOverride(Object obj){
        try {
            if(AopUtils.isAopProxy(obj)){
                obj = ((Advised)obj).getTargetSource().getTarget();
            }

            assert obj != null;
            Method afterUpdated = obj.getClass().getDeclaredMethod("afterUpdated", DocHV.class, Object.class, Object.class);
            return afterUpdated.getDeclaringClass() != NkCard.class;
        } catch (Exception e) {

            return false;
        }
    }
}
