package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocCalculateInterceptor;
import org.springframework.stereotype.Component;

@Component("NkDefaultDocCalculateInterceptor")
public class NkDefaultDocCalculateInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocCalculateInterceptor {
}