package cn.nkpro.ts5.engine.doc.interceptor;

import cn.nkpro.ts5.engine.co.NkCustomObject;
import cn.nkpro.ts5.engine.doc.NkDocCycle;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unused")
public interface NkDocCommittedInterceptor extends NkCustomObject {
    @Transactional(propagation = Propagation.NEVER)
    default void apply(DocHV doc, NkDocCycle cycle){}
}
