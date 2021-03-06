/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.platform.controller;

import cn.nkpro.elcube.annotation.NkNote;
import cn.nkpro.elcube.basic.NkProperties;
import cn.nkpro.elcube.platform.gen.UserSavedQuery;
import cn.nkpro.elcube.platform.model.WebMenuBO;
import cn.nkpro.elcube.platform.service.PlatformRegistryService;
import cn.nkpro.elcube.platform.service.UserQueryService;
import cn.nkpro.elcube.platform.service.WebMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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
    @Autowired@SuppressWarnings("all")
    private PlatformRegistryService constantService;

//    @Autowired@SuppressWarnings("all")
//    private NkMobileService nkMobileService;
//    @Autowired@SuppressWarnings("all")
//    private MobileOfficeAccProperties mobileOfficeAccProperties;

    private static String version = "0.0.0";

    static{
        try{
            URLClassLoader cl = (URLClassLoader) UserAppController.class.getClassLoader();
            URL url = cl.findResource("META-INF/MANIFEST.MF");
            if(url!=null){
                Manifest manifest = new Manifest(url.openStream());
                Attributes attributes = manifest.getMainAttributes();
                version = attributes.getValue("Implementation-Version");
            }
        }catch (Exception ignored){}
    }

    @NkNote("1.获取环境名称")
    @RequestMapping("/env")
    public Object env(){
        return new String[]{properties.getEnvName(),version};
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


//    @NkNote("4.App端发送手机验证码")
//    @RequestMapping("/app/send_verification_code")
//    public String sendVerificationCode(@RequestParam("phone") String phone){
//        return nkMobileService.sendVerificationCode(phone);
//    }
//
//    @NkNote("5.移动端登录绑定")
//    @RequestMapping("/app/bind")
//    public Map<String,Object> appBind(@RequestParam("NK-App") String nkApp,
//                                       @RequestParam(value = "phone",required = false) String phone,
//                                       @RequestParam(value = "verCode",required = false)String verCode,
//                                       @RequestParam(value = "openId",required = false)String openId,
//                                       @RequestParam(value = "appleId",required = false)String appleId){
//        return nkMobileService.appBind(nkApp,phone,verCode,openId,appleId);
//    }
//
//    @NkNote("6.微信code获取openId")
//    @RequestMapping("/app/queryOpenId")
//    public Map<String,Object> queryOpenId(@RequestParam("code") String code){
//        mobileOfficeAccProperties.setJsCode(code);
//        return nkMobileService.findOpenId(mobileOfficeAccProperties);
//    }
}
