package cn.nkpro.ts5.controller

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.utils.ResourceUtils
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@WsDocNote("T.测试")
@RequestMapping("/public/test")
@RestController
class TestController {

    @WsDocNote("测试")
    @RequestMapping("")
    String test(){


        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        def resource = resolver.getResource("cn/nkpro/ts5/cards/NkCardDate.vue")

        println ResourceUtils.readText(resource)

        return ResourceUtils.readText(resource)

    }
}
