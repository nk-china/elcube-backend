package cn.nkpro.ts5.controller;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.global.NkProperties;
import cn.nkpro.ts5.engine.web.UserQueryService;
import cn.nkpro.ts5.engine.web.WebMenuService;
import cn.nkpro.ts5.engine.web.model.WebMenuBO;
import cn.nkpro.ts5.orm.mb.gen.SysUserSavedQuery;
import cn.nkpro.ts5.orm.mb.gen.SysWebappMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */

@WsDocNote("2.UI控制器")
@RequestMapping("/webapp")
@RestController
public class WebAppController {

    @Autowired@SuppressWarnings("all")
    private NkProperties properties;

    @Autowired@SuppressWarnings("all")
    private WebMenuService tfmsSysWebappMenuService;

    @Autowired@SuppressWarnings("all")
    private UserQueryService userQueryService;


    @WsDocNote("1.获取环境名称")
    @RequestMapping("/env")
    public String env(){
        return properties.getEnvName();
    }

    @PreAuthorize("authenticated")
    @WsDocNote("2.加载Web主菜单")
    @RequestMapping("/menus")
    public List<WebMenuBO> menus(){
        return tfmsSysWebappMenuService.getMenus(true);
    }

    @PreAuthorize("authenticated")
    @WsDocNote("3.加载菜单详情")
    @RequestMapping("/menu/{id}")
    public SysWebappMenu menus(@PathVariable("id") String id){
        return tfmsSysWebappMenuService.getDetail(id);
    }

    @PreAuthorize("authenticated")
    @WsDocNote("31、获取保存的搜索列表")
    @RequestMapping("/user/saved/query/list")
    public List<SysUserSavedQuery> getList(String source){
        return userQueryService.getList(source);
    }

    @PreAuthorize("authenticated")
    @WsDocNote("32、保存的搜索条件")
    @RequestMapping("/user/saved/query/create")
    public SysUserSavedQuery create(@RequestBody SysUserSavedQuery query){
        userQueryService.create(query);
        return query;
    }

    @PreAuthorize("authenticated")
    @WsDocNote("33、删除已保存的搜索条件")
    @RequestMapping("/user/saved/query/delete")
    public void delete(String queryId){
        userQueryService.delete(queryId);
    }

}
