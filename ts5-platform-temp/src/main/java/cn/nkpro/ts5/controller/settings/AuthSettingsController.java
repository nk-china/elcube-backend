package cn.nkpro.ts5.controller.settings;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.ts5.model.SysAuthGroupBO;
import cn.nkpro.ts5.model.mb.gen.SysAccount;
import cn.nkpro.ts5.model.mb.gen.SysAuthGroup;
import cn.nkpro.ts5.model.mb.gen.SysAuthLimit;
import cn.nkpro.ts5.model.mb.gen.SysAuthPermission;
import cn.nkpro.ts5.services.TfmsPermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@WsDocNote("C2.权限设置")
@Controller
@RequestMapping("/settings/auth")
@PreAuthorize("hasAnyAuthority('*:*','SETTINGS:*','SETTINGS:AUTH')")
public class AuthSettingsController {

    @Autowired
    private TfmsPermService permService;

    @CompressResponse
    @RequestMapping("/limit/list")
    public List<SysAuthLimit> limits(){
        return permService.getLimits(null);
    }

    @CompressResponse
    @RequestMapping("/limit/detail")
    public SysAuthLimit limitDetail(String limitId){
        return permService.getLimitDetail(limitId);
    }

    @CompressResponse
    @RequestMapping("/limit/update")
    public SysAuthLimit limitUpdate(@RequestBody SysAuthLimit limit){
        permService.updateLimit(limit);
        return limit;
    }
    @ResponseBody
    @RequestMapping("/limit/remove")
    public void limitRemove(String limitId){
        permService.removeLimit(limitId);
    }

    @CompressResponse
    @RequestMapping("/perm/list")
    public List<SysAuthPermission> perms(){
        return permService.getPerms();
    }

    @CompressResponse
    @RequestMapping("/perm/detail")
    public SysAuthPermission permDetail(String permId){
        return permService.getPermDetail(permId);
    }

    @CompressResponse
    @RequestMapping("/perm/update")
    public SysAuthPermission permUpdate(@RequestBody SysAuthPermission perm){
        permService.updatePerm(perm);
        return perm;
    }
    @ResponseBody
    @RequestMapping("/perm/remove")
    public void permRemove(String permId){
        permService.removePerm(permId);
    }

    @CompressResponse
    @RequestMapping("/group/list")
    public List<SysAuthGroup> groups(){
        return permService.getGroups();
    }

    @CompressResponse
    @RequestMapping("/group/detail")
    public SysAuthGroup groupDetail(String groupId){
        return permService.getGroupDetail(groupId);
    }

    @CompressResponse
    @RequestMapping("/group/update")
    public SysAuthGroupBO groupUpdate(@RequestBody SysAuthGroupBO group){
        permService.updateGroup(group);
        return permService.getGroupDetail(group.getGroupId());
    }
    @ResponseBody
    @RequestMapping("/group/remove")
    public void groupRemove(String groupId){
        permService.removeGroup(groupId);
    }

    @CompressResponse
    @RequestMapping("/group/remove/account")
    public SysAuthGroup groupRemoveAccount(String groupId,String accountId){
        permService.removeAccountFromGroup(groupId,accountId);
        return permService.getGroupDetail(groupId);
    }

    @CompressResponse
    @RequestMapping("/group/add/account")
    public SysAuthGroup groupAddAccount(String groupId, String accountId){
        permService.addAccountFromGroup(groupId,accountId);
        return permService.getGroupDetail(groupId);
    }
    @CompressResponse
    @RequestMapping("/accounts")
    public List<SysAccount> accounts(String keyword){
        return permService.accounts(keyword);
    }
}
