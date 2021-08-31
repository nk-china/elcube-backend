package cn.nkpro.ts5.engine.doc.interceptor;


import cn.nkpro.ts5.engine.co.NkCustomObject;
import cn.nkpro.ts5.engine.doc.NkDocCycle;
import cn.nkpro.ts5.engine.doc.service.impl.NkDocTransactionProcessor;
import cn.nkpro.ts5.engine.doc.model.DocHV;

/**
 * Created by bean on 2020/7/13.
 */
@SuppressWarnings("unused")
public interface NkDocExecuteInterceptor extends NkCustomObject {
    default DocHV apply(DocHV doc, NkDocCycle cycle){return doc;}
}
