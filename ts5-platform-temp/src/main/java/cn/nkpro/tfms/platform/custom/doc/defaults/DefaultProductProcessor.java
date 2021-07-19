package cn.nkpro.tfms.platform.custom.doc.defaults;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import org.springframework.stereotype.Component;

@Component("nkDefaultProductProcessor")
public class DefaultProductProcessor extends DefaultStandaloneDocProcessor {

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.TRANSACTION;
    }
}
