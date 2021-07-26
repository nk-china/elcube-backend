package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.basic.NKCustomObjectManager;
import cn.nkpro.ts5.engine.doc.NKCard;
import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.model.DocIV;
import cn.nkpro.ts5.engine.doc.service.NKDocDefService;
import cn.nkpro.ts5.model.mb.gen.DocH;
import cn.nkpro.ts5.model.mb.gen.DocI;
import cn.nkpro.ts5.supports.GUID;
import cn.nkpro.ts5.supports.SequenceSupport;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import cn.nkpro.ts5.utils.VersioningUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("NKDocTransactionProcessor")
public class NKDocTransactionProcessor implements NKDocProcessor {

    @Autowired
    protected GUID guid;
    @Autowired
    private NKCustomObjectManager customObjectManager;
    @Autowired
    private NKDocDefService docDefService;
    @Autowired
    private SequenceSupport sequenceUtils;

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.TRANSACTION;
    }
    @Override
    public String desc() {
        return "交易";
    }

    public DocHV detail(String docId){
        return null;
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

        docDefService.doInCards(def,(card,defIV)->{

            DocIV docI = new DocIV();
            docI.setDocId(doc.getDocId());
            docI.setCardKey(defIV.getCardKey());
            docI.setCreatedTime(doc.getCreatedTime());
            docI.setData(card.create(doc,preDoc,defIV));

            doc.getData().put(defIV.getCardKey(),docI);

        });

        return doc;
    }

    @Override
    public DocHV detail(DocDefHV def, String docId) {
        return new DocHV();
    }

//    public DocHV doUpdate(DocHV doc, DocHV original){
//        if(original==null){
//            doc.setUpdatedTime(DateTimeUtilz.nowSeconds());
//            // 如果原始单据信息为空，那么表示单据为新建
//            if(StringUtils.isBlank(doc.getDocNumber()))
//                doc.setDocNumber(sequenceUtils.next(EnumDocClassify.valueOf(doc.getClassify()), doc.getDocType()));
//            bizDocMapper.insert(doc);
//        }
//
//        return doc;
//    }

    public DocHV calculate(DocHV doc, String fromCard, String options){
        return null;
    }

    public DocHV doUpdate(DocHV doc, String optSource){
        return null;
    }
}
