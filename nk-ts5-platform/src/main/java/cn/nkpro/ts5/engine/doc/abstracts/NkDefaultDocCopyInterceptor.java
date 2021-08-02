package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocCopyInterceptor;
import org.springframework.stereotype.Component;

@Component("NkDefaultDocCopyInterceptor")
public class NkDefaultDocCopyInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocCopyInterceptor {
}