package cn.nkpro.groovy.defaults

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject
import cn.nkpro.ts5.engine.doc.interceptor.NkDocCreateInterceptor
import org.springframework.stereotype.Component

@Component("NkDefaultDocCreateInterceptor")
class NkDefaultDocCreateInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocCreateInterceptor {
}