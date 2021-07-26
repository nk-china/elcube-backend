package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import org.springframework.stereotype.Component;

@Component("NKDocPartnerProcessor")
public class NKDocPartnerProcessor implements NKDocProcessor {
    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PARTNER;
    }

    @Override
    public DocHV toCreate(DocDefHV def, DocHV preDoc) {
        return new DocHV();
    }

    @Override
    public DocHV detail(DocDefHV def, String docId) {
        return new DocHV();
    }

    @Override
    public String desc() {
        return "交易伙伴";
    }
}
