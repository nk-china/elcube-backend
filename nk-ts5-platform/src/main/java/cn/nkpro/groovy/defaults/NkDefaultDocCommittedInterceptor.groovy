package cn.nkpro.groovy.defaults

import cn.nkpro.ts5.co.NkAbstractCustomScriptObject
import cn.nkpro.ts5.docengine.interceptor.NkDocCommittedInterceptor
import org.springframework.stereotype.Component

@Component("NkDefaultDocCommittedInterceptor")
class NkDefaultDocCommittedInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocCommittedInterceptor {
}