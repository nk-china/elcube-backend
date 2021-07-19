package cn.nkpro.tfms.platform.basis;

import cn.nkpro.tfms.platform.model.BizDocBase;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public abstract class TfmsSpELInjectionDocAwared implements TfmsSpELInjection {

    private BizDocBase doc;

    final void setDoc(BizDocBase doc) {
        this.doc = doc;
    }

    protected final BizDocBase doc(){
        return doc;
    }
}
