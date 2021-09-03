package cn.nkpro.groovy.defaults

import cn.nkpro.ts5.co.NkAbstractCustomScriptObject
import cn.nkpro.ts5.docengine.interceptor.NkDocFlowInterceptor
import org.springframework.stereotype.Component

@Component("NkDefaultDocFlowInterceptor")
class NkDefaultDocFlowInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocFlowInterceptor {
}
