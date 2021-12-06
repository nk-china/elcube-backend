/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.platform.controller;

import cn.nkpro.elcard.annotation.NkNote;
import cn.nkpro.elcard.basic.NkProperties;
import cn.nkpro.elcard.platform.gen.UserSavedQuery;
import cn.nkpro.elcard.platform.model.WebMenuBO;
import cn.nkpro.elcard.platform.service.PlatformRegistryService;
import cn.nkpro.elcard.platform.service.UserQueryService;
import cn.nkpro.elcard.platform.service.WebMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */

@NkNote("1.UI控制器")
@RequestMapping("/webapp")
@RestController
public class UserAppController {

    @Autowired@SuppressWarnings("all")
    private NkProperties properties;

    @Autowired@SuppressWarnings("all")
    private WebMenuService menuService;

    @Autowired@SuppressWarnings("all")
    private UserQueryService userQueryService;
    @Autowired
    private PlatformRegistryService constantService;


    @NkNote("1.获取环境名称")
    @RequestMapping("/env")
    public String env(){
        return properties.getEnvName();
    }

    @PreAuthorize("authenticated")
    @NkNote("2.加载Web主菜单")
    @RequestMapping("/menus")
    public List<WebMenuBO> menus(){
        return menuService.getMenus(true);
    }

    @PreAuthorize("authenticated")
    @NkNote("3.加载菜单详情")
    @RequestMapping("/menu/{id}")
    public String menus(@PathVariable("id") String id){
        return constantService.getString("@PAGE",id);
    }

    @PreAuthorize("authenticated")
    @NkNote("31、获取保存的搜索列表")
    @RequestMapping("/user/saved/query/list")
    public List<UserSavedQuery> getList(String source){
        return userQueryService.getList(source);
    }

    @PreAuthorize("authenticated")
    @NkNote("32、保存的搜索条件")
    @RequestMapping("/user/saved/query/create")
    public UserSavedQuery create(@RequestBody UserSavedQuery query){
        userQueryService.create(query);
        return query;
    }

    @PreAuthorize("authenticated")
    @NkNote("33、删除已保存的搜索条件")
    @RequestMapping("/user/saved/query/delete")
    public void delete(String queryId){
        userQueryService.delete(queryId);
    }

}
