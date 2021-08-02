package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocDeleteInterceptor;
import org.springframework.stereotype.Component;

@Component("NkDefaultDocDeleteInterceptor")
public class NkDefaultDocDeleteInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocDeleteInterceptor {
}