package cn.nkpro.groovy.defaults

import cn.nkpro.ts5.co.NkAbstractCustomScriptObject
import cn.nkpro.ts5.docengine.interceptor.NkDocUpdateInterceptor
import org.springframework.stereotype.Component

@Component("NkDefaultDocUpdateInterceptor")
class NkDefaultDocUpdateInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocUpdateInterceptor {
}