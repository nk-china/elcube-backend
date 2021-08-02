package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocFlowInterceptor;
import org.springframework.stereotype.Component;

@Component("NkDefaultDocFlowInterceptor")
public class NkDefaultDocFlowInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocFlowInterceptor {
}
