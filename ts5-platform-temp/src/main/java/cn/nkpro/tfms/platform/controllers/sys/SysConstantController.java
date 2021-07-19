package cn.nkpro.tfms.platform.controllers.sys;

import cn.nkpro.tfms.platform.model.po.DefConstant;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDefConstantService;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 */
@WsDocNote("D6.常量管理控制器")
@RequestMapping("/sys/constant")
@Controller
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:CONSTANT')")
public class SysConstantController {

    @Autowired
    private TfmsDefConstantService constantService;

    @CompressResponse
    @WsDocNote("11、加载常量列表")
    @RequestMapping("/list")
    public List<DefConstant> list(){
        return constantService.getAll();
    }


    @ResponseBody
    @CompressResponse
    @WsDocNote("12、更新")
    @RequestMapping("/save")
    public void save(@RequestBody List<DefConstant> list){
        constantService.doUpdate(list);
    }
}
