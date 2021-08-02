package cn.nkpro.groovy.defaults

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject
import cn.nkpro.ts5.engine.doc.interceptor.NkDocUpdateInterceptor
import org.springframework.stereotype.Component

@Component("NkDefaultDocUpdateInterceptor")
class NkDefaultDocUpdateInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocUpdateInterceptor {
}