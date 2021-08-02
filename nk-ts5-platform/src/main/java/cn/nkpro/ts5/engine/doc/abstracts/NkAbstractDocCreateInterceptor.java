package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocCreateInterceptor;
import org.springframework.stereotype.Component;

public abstract class NkAbstractDocCreateInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocCreateInterceptor {
}