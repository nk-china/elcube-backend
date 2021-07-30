package cn.nkpro.ts5.engine.doc.impl;

import org.springframework.stereotype.Component;

@Component("NKDocPartnerProcessor")
public class NkDocPartnerProcessor extends NkDocTransactionProcessor {
    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PARTNER;
    }

    @Override
    public String desc() {
        return "交易伙伴";
    }
}
