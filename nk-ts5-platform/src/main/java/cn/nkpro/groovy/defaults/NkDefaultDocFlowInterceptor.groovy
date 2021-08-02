package cn.nkpro.groovy.defaults

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject
import cn.nkpro.ts5.engine.doc.interceptor.NkDocFlowInterceptor
import org.springframework.stereotype.Component

@Component("NkDefaultDocFlowInterceptor")
class NkDefaultDocFlowInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocFlowInterceptor {
}
