package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.script.GroovyManager;
import cn.nkpro.ts5.engine.script.NKScriptEngine;
import cn.nkpro.ts5.engine.script.ScriptDefManager;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefH;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefHWithBLOBs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("D3.脚本配置")
@RestController
@RequestMapping("/def/script")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:SCRIPT')")
public class SysScriptDefController {

    @Autowired@SuppressWarnings("all")
    private NKScriptEngine scriptEngine;
    @Autowired@SuppressWarnings("all")
    private GroovyManager groovyManager;

    @Autowired@SuppressWarnings("all")
    private ScriptDefManager scriptService;

    @WsDocNote("1、获取脚本列表")
    @RequestMapping("/page")
    public PageList<ScriptDefH> page(
            @WsDocNote("查询关键字")
            @RequestParam(value = "keyword",    required = false)           String keyword,
            @WsDocNote("单据类型")
            @RequestParam(value = "version",    required = false)           String version,
            @WsDocNote("状态")
            @RequestParam(value = "state",      required = false)           String state,
            @WsDocNote("起始条目")
            @RequestParam(value = "from",       defaultValue = "0")         Integer from,
            @WsDocNote("条目数")
            @RequestParam(value = "rows",       defaultValue = "10")        Integer rows,
            @WsDocNote("排序字段")
            @RequestParam(value = "orderField", defaultValue = "")          String orderField,
            @WsDocNote("排序方式")
            @RequestParam(value = "order",      defaultValue = "")          String order){
        return scriptService.getPage(keyword,version,state,from,rows,orderField,order);
    }

    @WsDocNote("2、通过脚本ID获取脚本")
    @RequestMapping("/detail/{script}/{version}")
    public ScriptDefH detail(@PathVariable("script") String scriptName, @PathVariable String version){
        return scriptEngine.getRuntimeScript(scriptName,version);
    }

    @WsDocNote("4.预处理编辑")
    @RequestMapping("/edit")
    public ScriptDefH edit(
            @WsDocNote("脚本对象")@RequestBody ScriptDefHWithBLOBs script){
        return scriptService.doEdit(script);
    }

    @WsDocNote("5.更新")
    @RequestMapping("/update")
    public ScriptDefH update(
            @WsDocNote("脚本对象")@RequestBody ScriptDefHWithBLOBs script){
        return scriptService.doUpdate(script, false);
    }

    @WsDocNote("9.调试")
    @RequestMapping("/debug")
    public void debug(
            @WsDocNote("脚本对象")@RequestBody ScriptDefHWithBLOBs script){
        scriptEngine.setDebugScript(script);
    }
//
//    @WsDocNote("3、通过脚本名称获取脚本")
//    @CompressResponse
//    @RequestMapping("/name")
//    public DefScript getScriptByNname(String scriptName){
//        return scriptService.getScriptByName(scriptName);
//    }
//
//    @WsDocNote("4、保存脚本")
//    @CompressResponse
//    @RequestMapping("/update")
//    public DefScript update(@RequestBody DefScript script){
//        return scriptService.update(script);
//    }

    @WsDocNote("5.获取脚本的Groovy类名")
    @RequestMapping("/class/{beanName}")
    public NKScriptEngine.BeanDescribe className(@PathVariable("beanName") String beanName){
        return scriptEngine.getBeanDescribe(beanName);
    }
}
