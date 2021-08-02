package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocUpdateInterceptor;
import org.springframework.stereotype.Component;

public abstract class NkAbstractDocUpdateInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocUpdateInterceptor {
}