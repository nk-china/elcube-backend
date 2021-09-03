package cn.nkpro.ts5.docengine.interceptor;


import cn.nkpro.ts5.co.NkCustomObject;
import cn.nkpro.ts5.docengine.NkDocCycle;
import cn.nkpro.ts5.docengine.model.DocHV;

/**
 * Created by bean on 2020/7/13.
 */
@SuppressWarnings("unused")
public interface NkDocUpdateInterceptor extends NkCustomObject {
    default DocHV apply(DocHV doc, DocHV original, NkDocCycle cycle){return doc;}
}
