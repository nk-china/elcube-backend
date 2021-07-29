package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.devops.DebugSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("调试工具")
@RestController
@RequestMapping("/debug")
public class SysDebugController {

    @Autowired
    private DebugSupport debugSupport;

    @WsDocNote("1.获取正在调试的上下文列表")
    @RequestMapping("/contexts")
    public Collection<DebugSupport.DebugContext> list(){
        return debugSupport.getDebugContextList();
    }

    @WsDocNote("2.停止一个调试")
    @RequestMapping("/stop/{debugId}")
    public void stop(@PathVariable String debugId){
        debugSupport.stopDebugContext(debugId);
    }

    @WsDocNote("3.创建一个调试")
    @RequestMapping("/start")
    public String start(@RequestParam String desc){
        return debugSupport.startDebugContext(desc);
    }
}
