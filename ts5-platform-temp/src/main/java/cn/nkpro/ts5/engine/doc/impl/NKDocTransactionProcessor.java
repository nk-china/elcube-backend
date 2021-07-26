package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHD;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import cn.nkpro.ts5.orm.mb.gen.DocH;
import cn.nkpro.ts5.orm.mb.gen.DocHMapper;
import cn.nkpro.ts5.orm.mb.gen.DocI;
import cn.nkpro.ts5.orm.mb.gen.DocIMapper;
import cn.nkpro.ts5.config.id.GUID;
import cn.nkpro.ts5.config.id.SequenceSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.ts5.utils.VersioningUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("NKDocTransactionProcessor")
public class NKDocTransactionProcessor implements NKDocProcessor {

    @Autowired@SuppressWarnings("all")
    protected GUID guid;
    @Autowired@SuppressWarnings("all")
    private NKDocDefService docDefService;
    @Autowired@SuppressWarnings("all")
    private SequenceSupport sequenceUtils;
    @Autowired@SuppressWarnings("all")
    private DocHMapper docHMapper;
    @Autowired@SuppressWarnings("all")
    private DocIMapper docIMapper;
    @Autowired
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
        doc.setDefVersion(VersioningUtils.parseMajorInteger(def.getVersion()));

        // 引用数据
        //doc.setPreDoc(preDoc);
        doc.setDef(def);

        docDefService.runLoopCards(def,(card, defIV)->
                doc.getData().put(defIV.getCardKey(),card.create(doc,preDoc,defIV))
        );

        return doc;
    }

    @Override
    public DocHV calculate(DocHV doc, String fromCard, String options) throws Exception {
        docDefService.runLoopCards(doc.getDef(),(card, defIV)->{
            String _options = StringUtils.equals(fromCard,defIV.getCardKey())?options:null;
            doc.getData().put(defIV.getCardKey(),card.calculate(doc,defIV,_options));
        });
        return doc;
    }

    @Override
    public DocHV detail(DocDefHV def, DocHD docHD) throws Exception {

        DocHV doc = BeanUtilz.copyFromObject(docHD, DocHV.class);
        doc.setDef(def);

        // 解析单据行项目数据
        docDefService.runLoopCards(def,(nkCard, docDefI)->{

            // 获取行项目数据
            Optional.ofNullable(doc.getItems().get(docDefI.getCardKey()))
                .ifPresent((docI -> {
                    // 调用卡片程序解析数据
                    Object dataI = nkCard.afterGetData(doc, docI.getCardContent(), docDefI.getCardContent());
                    doc.getData().put(docDefI.getCardKey(),dataI);
                    // 清除JSON，避免无效数据网络传输
                    docI.setCardContent(null);
                }));
        });

        return doc;
    }

    @Override
    public DocHV doUpdate(DocDefHV def, DocHV doc, DocHV original, String optSource) throws Exception {
        Optional<DocHV> optional = Optional.ofNullable(original);

        doc.setUpdatedTime(DateTimeUtilz.nowSeconds());

        if(!optional.isPresent()){
            if(StringUtils.isBlank(doc.getDocNumber()))
                doc.setDocNumber(sequenceUtils.next(EnumDocClassify.valueOf(doc.getClassify()), doc.getDocType()));

            doc.setCreatedTime(doc.getUpdatedTime());
            docHMapper.insert(doc);
        }

        // todo 检查单据状态是否合法，即符合单据配置中的 状态流

        docDefService.runLoopCards(def,(card, defIV)->{

            // 如果原始单据数据存在则更新，否则插入数据
            if(original !=null && original.getItems().containsKey(defIV.getCardKey())){

                DocI docI = BeanUtilz.copyFromObject(original.getItems().get(defIV.getCardKey()),DocI.class);
                docI.setCardContent(JSON.toJSONString(doc.getData().get(defIV.getCardKey())));

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
                docI.setCardContent(JSON.toJSONString(doc.getData().get(defIV.getCardKey())));

                docIMapper.insert(docI);

                // 将更新后的数据放回到doc
                doc.getItems().put(defIV.getCardKey(),docI);
                docI.setCardContent(null);
            }
        });

        docHMapper.updateByPrimaryKeySelective(doc);

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

//        EvaluationContext context = spELManager.createContext(docBO);
//        docBO.getDefinedDoc().getIndexRules()
//                .forEach(indexRule -> {
//                    Expression exp = spELManager.parser().parseExpression(indexRule.getRule());
//                    setEsDocFiled(docIndex,indexRule.getField(),exp.getValue(context));
//                });

        searchEngine.indexBeforeCommit(docHES);
    }
}
