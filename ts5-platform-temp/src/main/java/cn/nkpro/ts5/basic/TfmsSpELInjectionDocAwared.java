package cn.nkpro.ts5.basic;

import cn.nkpro.ts5.engine.doc.model.DocHV;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public abstract class TfmsSpELInjectionDocAwared implements TfmsSpELInjection {

    private DocHV doc;

    final void setDoc(DocHV doc) {
        this.doc = doc;
    }

    protected final DocHV doc(){
        return doc;
    }
}
