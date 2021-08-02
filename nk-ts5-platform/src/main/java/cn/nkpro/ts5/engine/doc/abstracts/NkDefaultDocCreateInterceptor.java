package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocCreateInterceptor;
import org.springframework.stereotype.Component;

@Component("NkDefaultDocCreateInterceptor")
public class NkDefaultDocCreateInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocCreateInterceptor {
}