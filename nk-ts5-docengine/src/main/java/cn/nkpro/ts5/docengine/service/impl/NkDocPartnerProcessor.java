package cn.nkpro.ts5.docengine.service.impl;

import cn.nkpro.ts5.docengine.NkDocProcessor;
import org.springframework.stereotype.Component;

@Component("NKDocPartnerProcessor")
public class NkDocPartnerProcessor extends NkDocTransactionProcessor {
    @Override
    public NkDocProcessor.EnumDocClassify classify() {
        return NkDocProcessor.EnumDocClassify.PARTNER;
    }

    @Override
    public String desc() {
        return "交易伙伴";
    }
}
