package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.script.NKScriptEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by bean on 2020/1/15.
 */
@WsDocNote("D2.卡片配置")
@RestController
@RequestMapping("/def/resources")
public class SysCardResController {

    @Autowired@SuppressWarnings("all")
    private NKScriptEngine scriptEngine;

    @WsDocNote("1.获取卡片信息")
    @RequestMapping("/vue")
    public Map<String, String> vueTemplates() {
        return scriptEngine.getRuntimeVueMap();
    }
}
