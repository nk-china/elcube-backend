package cn.nkpro.groovy.defaults

import cn.nkpro.ts5.co.NkAbstractCustomScriptObject
import cn.nkpro.ts5.docengine.interceptor.NkDocStateInterceptor
import org.springframework.stereotype.Component

@Component("NkDefaultDocStateInterceptor")
class NkDefaultDocStateInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocStateInterceptor {
}
