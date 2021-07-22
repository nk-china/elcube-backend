package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import org.springframework.stereotype.Component;

@Component("NKDocPartnerProcessor")
public class NKDocPartnerProcessor implements NKDocProcessor {
    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PARTNER;
    }

    @Override
    public String desc() {
        return "交易伙伴";
    }
}
