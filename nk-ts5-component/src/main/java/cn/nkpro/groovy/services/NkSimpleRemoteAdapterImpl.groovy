package cn.nkpro.groovy.services

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.co.remote.NkAbstractRemoteAdapter
import org.springframework.stereotype.Component

@NkNote("演示远程调用")
@Component("NkSimpleRemoteAdapterImpl")
class NkSimpleRemoteAdapterImpl extends NkAbstractRemoteAdapter<Map,Map>{
    @Override
    Map<String,Object> doApply(Map options) {

        if(options!=null){

            Map clone = new HashMap(options)
            clone.put("age",30)
            return clone
        }else{

            return Collections.singletonMap("age", 31)

        }
    }
}
