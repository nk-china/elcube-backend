package cn.nkpro.tfms.platform.custom.doc.defaults;

import cn.nkpro.tfms.platform.model.BizDocBase;
import org.springframework.stereotype.Component;

@Component("nkDefaultProjectQuotationProcessor")
public class DefaultProjectQuotationProcessor extends DefaultProjectTransactionProcessor {

    @Override
    public Class<? extends BizDocBase> dataType() {
        return BizProjectQuotationDoc.class;
    }

    @Override
    public BizDocBase detailAfterComponent(BizDocBase doc) {
        return super.detailAfterComponent(doc);
    }
}
