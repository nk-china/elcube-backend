package cn.nkpro.ts5.controller.settings;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.ts5.model.SysWebappMenuBO;
import cn.nkpro.ts5.services.TfmsSysWebappMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */
@WsDocNote("C1.菜单设置")
@RequestMapping("/settings/menu")
@Controller
@PreAuthorize("hasAnyAuthority('*:*','SETTINGS:*','SETTINGS:MENU')")
public class MenuSettingsController {

    @Autowired@SuppressWarnings("all")
    private TfmsSysWebappMenuService webappMenuService;

    @WsDocNote("1.加载Web主菜单")
    @CompressResponse
    @RequestMapping("/menus")
    public List<SysWebappMenuBO> menus(){
        return webappMenuService.getMenus(false);
    }


    @WsDocNote("2.更新菜单")
    @ResponseBody
    @RequestMapping("/save")
    public void save(@RequestBody List<SysWebappMenuBO> menus){
        webappMenuService.doUpdate(menus);
    }
}
