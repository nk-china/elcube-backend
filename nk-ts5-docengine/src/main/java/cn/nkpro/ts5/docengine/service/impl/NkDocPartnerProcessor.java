package cn.nkpro.ts5.docengine.service.impl;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.docengine.NkDocProcessor;
import org.springframework.stereotype.Component;

@NkNote("交易伙伴")
@Component("NKDocPartnerProcessor")
public class NkDocPartnerProcessor extends NkDocTransactionProcessor {
    @Override
    public NkDocProcessor.EnumDocClassify classify() {
        return NkDocProcessor.EnumDocClassify.PARTNER;
    }
}
