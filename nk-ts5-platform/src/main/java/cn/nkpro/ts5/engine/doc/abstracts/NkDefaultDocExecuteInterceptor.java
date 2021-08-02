package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocExecuteInterceptor;
import org.springframework.stereotype.Component;

@Component("NkDefaultDocExecuteInterceptor")
public class NkDefaultDocExecuteInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocExecuteInterceptor {
}