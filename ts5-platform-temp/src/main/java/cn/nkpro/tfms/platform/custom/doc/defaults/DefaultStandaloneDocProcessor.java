package cn.nkpro.tfms.platform.custom.doc.defaults;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.doc.AbstractDocProcessor;
import cn.nkpro.tfms.platform.model.BizDocBase;
import org.springframework.stereotype.Component;

@Component("nkDefaultStandaloneDocProcessor")
public class DefaultStandaloneDocProcessor extends AbstractDocProcessor {

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.TRANSACTION;
    }

    @Override
    public boolean standalone() {
        return true;
    }

    @Override
    public BizDocBase updateAfterComponent(BizDocBase doc, boolean isCreate) {
        return super.updateAfterComponent(doc, isCreate);
    }
}
