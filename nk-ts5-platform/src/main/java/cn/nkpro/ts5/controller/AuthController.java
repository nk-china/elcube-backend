package cn.nkpro.ts5.controller;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.security.TfmsUserDetails;
import cn.nkpro.ts5.engine.web.UserAccountService;
import cn.nkpro.ts5.engine.web.UserAuthorizationService;
import cn.nkpro.ts5.orm.mb.gen.SysAuthLimit;
import cn.nkpro.ts5.config.security.SecurityUtilz;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2019/12/30.
 */

@PreAuthorize("authenticated")
@WsDocNote("1.用户验证服务")
@RestController
@RequestMapping("/authentication")
public class AuthController {

    @Qualifier("NkSysAccountService")
    @Autowired@SuppressWarnings("all")
    private UserAccountService tfmsSysAccountService;

    @Autowired@SuppressWarnings("all")
    private UserAuthorizationService permService;

    @PreAuthorize("hasAnyAuthority('*:*','SYS:LOGIN')")
    @WsDocNote("1.获取token登陆")
    @RequestMapping("/token")
    public Map<String,Object> token(){
        return tfmsSysAccountService.createToken();
    }

    @WsDocNote("2.刷新token")
    @RequestMapping("/refresh_token")
    public Map<String, Object> refreshToken(){
        return tfmsSysAccountService.refreshToken();
    }

    @WsDocNote("3.清除token并退出")
    @RequestMapping("/clear")
    public void clear(){
        tfmsSysAccountService.clear();
    }

    @WsDocNote("4.获取用户信息")
    @RequestMapping("/info")
    public TfmsUserDetails info(){
        TfmsUserDetails user = SecurityUtilz.getUser();
        user.setPassword(null);
        return user;
    }

    @WsDocNote("5.获取用户授权限制")
    @RequestMapping("/info/limits")
    public List<SysAuthLimit> limits(@RequestBody String[] limitIds){
        if(ArrayUtils.isEmpty(limitIds)){
            return Collections.emptyList();
        }
        return permService.getLimits(limitIds);
    }

    @WsDocNote("6.修改密码")
    @RequestMapping("/change_password")
    public void changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword")String newPassword){
        tfmsSysAccountService.doChangePassword(SecurityUtilz.getUser().getId(),oldPassword,newPassword);
    }
}
