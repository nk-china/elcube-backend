package cn.nkpro.ts5.controller;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.mvc.CompressObject;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.ts5.config.security.TfmsUserDetails;
import cn.nkpro.ts5.model.mb.gen.SysAuthLimit;
import cn.nkpro.ts5.services.TfmsPermService;
import cn.nkpro.ts5.services.TfmsSysAccountService;
import cn.nkpro.ts5.utils.SecurityUtilz;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2019/12/30.
 */
@WsDocNote("1.用户验证服务")
@Controller
@RequestMapping("/authentication")
public class AuthController {

    @Qualifier("NkSysAccountService")
    @Autowired
    private TfmsSysAccountService tfmsSysAccountService;

    @Autowired
    private TfmsPermService permService;

    @ResponseBody
    @CompressResponse
    @RequestMapping("/token")
    public Map<String,Object> token(){
        return tfmsSysAccountService.createToken();
    }

    @CompressResponse
    @RequestMapping("/info")
    public TfmsUserDetails info(){
        TfmsUserDetails user = SecurityUtilz.getUser();
        user.setPassword(null);
        return user;
    }

    @CompressResponse
    @RequestMapping("/info/limits")
    public List<SysAuthLimit> limits(@RequestBody String[] limitIds){
        if(ArrayUtils.isEmpty(limitIds)){
            return Collections.emptyList();
        }
        return permService.getLimits(limitIds);
    }

    @ResponseBody
    @RequestMapping("/clear")
    public void clear(){
        tfmsSysAccountService.clear();
    }

    @CompressResponse
    @RequestMapping("/refresh_token")
    public CompressObject refreshToken(){
        return CompressObject.valueOf(tfmsSysAccountService.refreshToken());
    }

    @ResponseBody
    @RequestMapping("/change_password")
    public void changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword")String newPassword){
        tfmsSysAccountService.doChangePassword(SecurityUtilz.getUser().getId(),oldPassword,newPassword);
    }
}
