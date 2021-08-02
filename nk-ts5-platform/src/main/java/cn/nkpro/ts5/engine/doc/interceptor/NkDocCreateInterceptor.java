package cn.nkpro.ts5.engine.doc.interceptor;


import cn.nkpro.ts5.engine.co.NkCustomObject;
import cn.nkpro.ts5.engine.doc.NkDocCycle;
import cn.nkpro.ts5.engine.doc.model.DocHV;

/**
 * Created by bean on 2020/7/13.
 */
@SuppressWarnings("unused")
public interface NkDocCreateInterceptor extends NkCustomObject {
    default DocHV apply(DocHV doc, DocHV ref, NkDocCycle cycle){return doc;}
}
