package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.co.DebugContextManager;
import cn.nkpro.ts5.engine.doc.NkDocEngine;
import cn.nkpro.ts5.engine.spel.TfmsSpELManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("31.[DevDef]调试工具")
@RestController
@RequestMapping("/debug")
public class SysDebugController {

    @Autowired
    private DebugContextManager debugSupport;
    @Autowired
    private TfmsSpELManager spELManager;
    @Autowired
    private NkDocEngine docEngine;

    @WsDocNote("1.获取正在调试的上下文列表")
    @RequestMapping("/contexts")
    public Collection<DebugContextManager.ContextDescribe> list(){
        return debugSupport.getDebugContextList();
    }

    @WsDocNote("2.停止一个调试")
    @RequestMapping("/stop/{debugId}")
    public void stop(@PathVariable String debugId){
        debugSupport.removeContext(debugId);
    }

    @WsDocNote("3.创建一个调试")
    @RequestMapping("/start")
    public String start(@RequestParam String desc){
        return debugSupport.createContext(desc);
    }

    @WsDocNote("4.获取调试中的资源")
    @RequestMapping("/resources")
    public List<Object> resources(){
        return debugSupport.getDebugResources("@","#");
    }

    @WsDocNote("5.调试SpEL")
    @RequestMapping("/spel/test")
    public R spELTest(
            @RequestParam String el,
            @RequestParam(required = false)String docId,
            @RequestParam(required = false,defaultValue = "false")boolean isTemplate){

        try{
            EvaluationContext context = spELManager.createContext(StringUtils.isNotBlank(docId)?docEngine.detail(docId):null);
            if(isTemplate){
                return new R(spELManager.convert(el, context),null,null);
            }else {
                return new R(spELManager.invoke(el, context),null,null);
            }
        }catch (Exception e){
            return new R(null, e.getMessage(), ExceptionUtils.getRootCauseStackTrace(e));
        }
    }

    @Data
    @AllArgsConstructor
    static class R{
        Object result;
        String errorMessage;
        String[] causeStackTrace;
    }
}
