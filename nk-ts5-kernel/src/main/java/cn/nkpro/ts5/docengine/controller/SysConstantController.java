package cn.nkpro.ts5.docengine.controller;

import cn.nkpro.ts5.docengine.gen.ConstantDef;
import cn.nkpro.ts5.docengine.service.NkConstantService;
import cn.nkpro.ts5.wsdoc.annotation.WsDocNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 */
@WsDocNote("32.[DevDef]常量")
@RequestMapping("/sys/constant")
@RestController
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:CONSTANT')")
public class SysConstantController {

    @Autowired
    private NkConstantService constantService;

    @WsDocNote("11、加载常量列表")
    @RequestMapping("/list")
    public List<ConstantDef> list(){
        return constantService.getAll();
    }

    @WsDocNote("12、更新")
    @RequestMapping("/save")
    public void save(@RequestBody List<ConstantDef> list){
        constantService.doUpdate(list);
    }
}
