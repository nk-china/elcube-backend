package cn.nkpro.ts5.controller;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.ts5.config.nk.NKProperties;
import cn.nkpro.ts5.model.SysWebappMenuBO;
import cn.nkpro.ts5.model.mb.gen.SysUserSavedQuery;
import cn.nkpro.ts5.model.mb.gen.SysWebappMenu;
import cn.nkpro.ts5.services.TfmsSysWebappMenuService;
import cn.nkpro.ts5.services.TfmsUserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */
@WsDocNote("2.UI控制器")
@RequestMapping("/webapp")
@Controller
public class WebAppController {

    @Autowired@SuppressWarnings("all")
    private NKProperties properties;

    @Autowired@SuppressWarnings("all")
    private TfmsSysWebappMenuService tfmsSysWebappMenuService;

    @Autowired@SuppressWarnings("all")
    private TfmsUserQueryService userQueryService;


    @WsDocNote("1.获取环境名称")
    @ResponseBody
    @RequestMapping("/env")
    public String env(){
        return properties.getEnvName();
    }

    @WsDocNote("2.加载Web主菜单")
    @CompressResponse
    @RequestMapping("/menus")
    public List<SysWebappMenuBO> menus(){
        return tfmsSysWebappMenuService.getMenus(true);
    }

    @WsDocNote("3.加载菜单详情")
    @CompressResponse
    @RequestMapping("/menu/{id}")
    public SysWebappMenu menus(@PathVariable("id") String id){
        return tfmsSysWebappMenuService.getDetail(id);
    }

    @WsDocNote("31、获取保存的搜索列表")
    @CompressResponse
    @RequestMapping("/user/saved/query/list")
    public List<SysUserSavedQuery> getList(String source){
        return userQueryService.getList(source);
    }

    @WsDocNote("32、保存的搜索条件")
    @CompressResponse
    @RequestMapping("/user/saved/query/create")
    public SysUserSavedQuery create(@RequestBody SysUserSavedQuery query){
        userQueryService.create(query);
        return query;
    }

    @WsDocNote("33、删除已保存的搜索条件")
    @CompressResponse
    @RequestMapping("/user/saved/query/delete")
    public void delete(String queryId){
        userQueryService.delete(queryId);
    }

}
