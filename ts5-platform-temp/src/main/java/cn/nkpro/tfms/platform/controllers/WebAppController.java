package cn.nkpro.tfms.platform.controllers;

import cn.nkpro.tfms.platform.model.SysWebappMenuBO;
import cn.nkpro.tfms.platform.model.po.DefDocComponentKey;
import cn.nkpro.tfms.platform.model.po.SysUserSavedQuery;
import cn.nkpro.tfms.platform.model.po.SysWebappMenu;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDefDocTypeService;
import cn.nkpro.tfms.platform.services.TfmsSysWebappMenuService;
import cn.nkpro.tfms.platform.services.TfmsUserQueryService;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.nk.NKProperties;
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

    private NKProperties toolkitProperties;

    private TfmsSysWebappMenuService tfmsSysWebappMenuService;

    private TfmsUserQueryService userQueryService;

    private TfmsDefDocTypeService defDocTypeService;


    @Autowired
    public WebAppController(NKProperties toolkitProperties,
                            TfmsSysWebappMenuService tfmsSysWebappMenuService,
                            TfmsUserQueryService userQueryService,
                            TfmsDefDocTypeService defDocTypeService) {
        this.toolkitProperties = toolkitProperties;
        this.tfmsSysWebappMenuService = tfmsSysWebappMenuService;
        this.userQueryService = userQueryService;
        this.defDocTypeService = defDocTypeService;
    }

    @WsDocNote("11、加载Web主菜单")
    @CompressResponse
    @ResponseBody
    @RequestMapping("/env")
    public String env(){
        return toolkitProperties.getEnvName();
    }

    @WsDocNote("11、加载Web主菜单")
    @CompressResponse
    @RequestMapping("/menus")
    public List<SysWebappMenuBO> menus(){
        return tfmsSysWebappMenuService.getMenus(true);
    }

    @WsDocNote("12、加载菜单详情")
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

    @WsDocNote("41、获取配置文档")
    @ResponseBody
    @RequestMapping("/document")
    public String documents(@RequestBody DefDocComponentKey key){
        return defDocTypeService.getDocComponentDefMarkdown(key);
    }

}
