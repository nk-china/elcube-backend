package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.service.NKConstantService;
import cn.nkpro.ts5.model.mb.gen.ConstantDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 */
@WsDocNote("D6.常量管理控制器")
@RequestMapping("/sys/constant")
@RestController
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:CONSTANT')")
public class SysConstantController {

    @Autowired
    private NKConstantService constantService;

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
