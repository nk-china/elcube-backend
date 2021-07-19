package cn.nkpro.tfms.platform.controllers.sys;

import cn.nkpro.tfms.platform.model.po.DefScript;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDefScriptService;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("D4.脚本配置")
@Controller
@RequestMapping("/def/script")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:SCRIPT')")
public class SysScriptDefController {

    @Autowired
    private TfmsDefScriptService scriptService;

    @WsDocNote("1、获取脚本列表")
    @CompressResponse
    @RequestMapping("/list")
    public List<DefScript> list(){
        return scriptService.getAll();
    }

    @WsDocNote("2、通过脚本ID获取脚本")
    @CompressResponse
    @RequestMapping("/get")
    public DefScript getScript(String scriptId){
        return scriptService.getScript(scriptId);
    }

    @WsDocNote("3、通过脚本名称获取脚本")
    @CompressResponse
    @RequestMapping("/name")
    public DefScript getScriptByNname(String scriptName){
        return scriptService.getScriptByName(scriptName);
    }

    @WsDocNote("4、保存脚本")
    @CompressResponse
    @RequestMapping("/update")
    public DefScript update(@RequestBody DefScript script){
        return scriptService.update(script);
    }

    @WsDocNote("5、获取脚本的Groovy类名")
    @CompressResponse
    @ResponseBody
    @RequestMapping("/class/{beanName}")
    public String className(@PathVariable("beanName") String beanName){
        return scriptService.getClassName(beanName);
    }
}
