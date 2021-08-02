package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocStateInterceptor;
import org.springframework.stereotype.Component;

@Component("NkDefaultDocStateInterceptor")
public class NkDefaultDocStateInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocStateInterceptor {
}
