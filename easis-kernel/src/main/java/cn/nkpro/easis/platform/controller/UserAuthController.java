/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.platform.controller;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.security.SecurityUtilz;
import cn.nkpro.easis.security.UserAccountService;
import cn.nkpro.easis.security.UserAuthorizationService;
import cn.nkpro.easis.security.bo.UserDetails;
import cn.nkpro.easis.security.gen.AuthLimit;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2019/12/30.
 */

@PreAuthorize("authenticated")
@NkNote("2.用户验证服务")
@RestController
@RequestMapping("/authentication")
public class UserAuthController {

    @Qualifier("NkSysAccountService")
    @Autowired@SuppressWarnings("all")
    private UserAccountService tfmsSysAccountService;

    @Autowired@SuppressWarnings("all")
    private UserAuthorizationService permService;

    @PreAuthorize("hasAnyAuthority('*:*','SYS:LOGIN')")
    @NkNote("1.获取token登陆")
    @RequestMapping("/token")
    public Map<String,Object> token(){
        return tfmsSysAccountService.createToken();
    }

    @NkNote("2.刷新token")
    @RequestMapping("/refresh_token")
    public Map<String, Object> refreshToken(){
        return tfmsSysAccountService.refreshToken();
    }

    @NkNote("3.清除token并退出")
    @RequestMapping("/clear")
    public void clear(){
        tfmsSysAccountService.clear();
    }

    @NkNote("4.获取用户信息")
    @RequestMapping("/info")
    public UserDetails info(){
        UserDetails user = SecurityUtilz.getUser();
        user.setPassword(null);
        return user;
    }

    @NkNote("5.获取用户授权限制")
    @RequestMapping("/info/limits")
    public List<AuthLimit> limits(@RequestBody String[] limitIds){
        if(ArrayUtils.isEmpty(limitIds)){
            return Collections.emptyList();
        }
        return permService.getLimits(limitIds);
    }

    @NkNote("6.修改密码")
    @RequestMapping("/change_password")
    public void changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword")String newPassword){
        tfmsSysAccountService.doChangePassword(SecurityUtilz.getUser().getId(),oldPassword,newPassword);
    }
}
