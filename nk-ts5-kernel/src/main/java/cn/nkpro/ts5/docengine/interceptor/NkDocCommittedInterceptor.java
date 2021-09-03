package cn.nkpro.ts5.docengine.interceptor;

import cn.nkpro.ts5.co.NkCustomObject;
import cn.nkpro.ts5.docengine.NkDocCycle;
import cn.nkpro.ts5.docengine.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unused")
public interface NkDocCommittedInterceptor extends NkCustomObject {
    @Transactional(propagation = Propagation.NEVER)
    default void apply(DocHV doc, NkDocCycle cycle){}
}
