package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.interceptor.NkDocCommittedInterceptor;
import org.springframework.stereotype.Component;

@Component("NkDefaultDocCommittedInterceptor")
public class NkDefaultDocCommittedInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocCommittedInterceptor {
}