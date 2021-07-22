package cn.nkpro.ts5.engine.doc.impl;

import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import org.springframework.stereotype.Component;

@Component("NKDocTransactionProcessor")
public class NKDocTransactionProcessor implements NKDocProcessor {
    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.TRANSACTION;
    }
    @Override
    public String desc() {
        return "交易";
    }
}
