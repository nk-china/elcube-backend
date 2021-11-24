package cn.nkpro.ts5.docengine.service.impl;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.docengine.EnumDocClassify;
import cn.nkpro.ts5.docengine.NkDocProcessor;
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
}
