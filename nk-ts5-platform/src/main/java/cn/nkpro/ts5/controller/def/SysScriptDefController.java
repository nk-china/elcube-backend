package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.model.ScriptDefHV;
import cn.nkpro.ts5.engine.doc.service.NkScriptDefManager;
import cn.nkpro.ts5.orm.mb.gen.ScriptDefH;
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
    private NkScriptDefManager scriptService;

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
        return scriptService.getScript(scriptName,version);
    }

    @WsDocNote("4.复制")
    @RequestMapping("/breach")
    public ScriptDefH breach(
            @WsDocNote("脚本对象")@RequestBody ScriptDefHV script){
        return scriptService.doBreach(script);
    }

    @WsDocNote("5.更新")
    @RequestMapping("/update")
    public ScriptDefH update(
            @WsDocNote("脚本对象")@RequestBody ScriptDefHV script){
        return scriptService.doUpdate(script, false);
    }

    @WsDocNote("6.激活")
    @RequestMapping("/active")
    public ScriptDefH active(
            @WsDocNote("脚本对象")@RequestBody ScriptDefHV script){
        return scriptService.doActive(script);
    }

    @WsDocNote("7.删除")
    @RequestMapping("/delete")
    public void delete(
            @WsDocNote("脚本对象")@RequestBody ScriptDefHV script){
        scriptService.doDelete(script);
    }

    @WsDocNote("8.调试")
    @RequestMapping("/debug")
    public ScriptDefH debug(
            @WsDocNote("脚本对象")@RequestBody ScriptDefHV script){
        return scriptService.doRun(script);
    }
}
