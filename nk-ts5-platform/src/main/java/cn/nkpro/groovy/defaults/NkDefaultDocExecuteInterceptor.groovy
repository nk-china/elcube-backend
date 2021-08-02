package cn.nkpro.groovy.defaults

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject
import cn.nkpro.ts5.engine.doc.interceptor.NkDocExecuteInterceptor
import org.springframework.stereotype.Component

@Component("NkDefaultDocExecuteInterceptor")
class NkDefaultDocExecuteInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocExecuteInterceptor {
}