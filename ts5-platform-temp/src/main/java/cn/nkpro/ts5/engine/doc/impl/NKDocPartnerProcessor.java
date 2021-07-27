package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import org.springframework.stereotype.Component;

@Component("NKDocPartnerProcessor")
public class NKDocPartnerProcessor extends NKDocTransactionProcessor {
    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PARTNER;
    }

    @Override
    public String desc() {
        return "交易伙伴";
    }

    @Override
    public DocHV toCreate(DocDefHV def, DocHV preDoc) throws Exception {
        DocHV docHV = super.toCreate(def, preDoc);
        return docHV;
    }
}
