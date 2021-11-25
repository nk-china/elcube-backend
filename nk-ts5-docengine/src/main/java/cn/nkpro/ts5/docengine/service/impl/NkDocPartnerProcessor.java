package cn.nkpro.ts5.docengine.service.impl;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.docengine.EnumDocClassify;
import cn.nkpro.ts5.docengine.NkDocProcessor;
import cn.nkpro.ts5.docengine.model.DocDefHV;
import cn.nkpro.ts5.docengine.model.DocHV;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@NkNote("伙伴")
@Component("NKDocPartnerProcessor")
public class NkDocPartnerProcessor extends NkDocTransactionProcessor {
    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PARTNER;
    }

    @Override
    public String desc() {
        return "Partner | 伙伴";
    }

    @Override
    public DocHV toCreate(DocDefHV def, DocHV preDoc) {
        DocHV doc = super.toCreate(def, preDoc);
        doc.setDocName(null);
        return doc;
    }

    @Override
    public DocHV doUpdate(DocHV doc, DocHV original, String optSource) {
        doc.setPartnerName(doc.getDocName());
        doc =  super.doUpdate(doc, original, optSource);
        return doc;
    }
}
