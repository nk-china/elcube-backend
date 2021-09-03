package cn.nkpro.groovy.defaults

import cn.nkpro.ts5.co.NkAbstractCustomScriptObject
import cn.nkpro.ts5.docengine.interceptor.NkDocExecuteInterceptor
import org.springframework.stereotype.Component

@Component("NkDefaultDocExecuteInterceptor")
class NkDefaultDocExecuteInterceptor
        extends NkAbstractCustomScriptObject
        implements NkDocExecuteInterceptor {
}