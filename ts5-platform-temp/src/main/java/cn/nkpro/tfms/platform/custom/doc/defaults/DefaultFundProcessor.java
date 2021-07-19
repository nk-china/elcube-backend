package cn.nkpro.tfms.platform.custom.doc.defaults;

import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.model.BizDocBase;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("nkDefaultFundProcessor")
public class DefaultFundProcessor extends DefaultProjectProcessor {

    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PROJECT;
    }
}