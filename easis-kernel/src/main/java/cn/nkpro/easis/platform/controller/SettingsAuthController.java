package cn.nkpro.easis.platform.controller;

import cn.nkpro.easis.platform.gen.UserAccount;
import cn.nkpro.easis.security.UserAuthorizationService;
import cn.nkpro.easis.security.bo.UserGroupBO;
import cn.nkpro.easis.security.gen.AuthGroup;
import cn.nkpro.easis.security.gen.AuthLimit;
import cn.nkpro.easis.security.gen.AuthPermission;
import cn.nkpro.easis.annotation.NkNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NkNote("12.权限设置")
@RestController
@RequestMapping("/settings/auth")
@PreAuthorize("hasAnyAuthority('*:*','SETTINGS:*','SETTINGS:AUTH')")
public class SettingsAuthController {

    @Autowired@SuppressWarnings("all")
    private UserAuthorizationService permService;
    
    @RequestMapping("/limit/list")
    public List<AuthLimit> limits(){
        return permService.getLimits(null);
    }
    
    @RequestMapping("/limit/detail")
    public AuthLimit limitDetail(String limitId){
        return permService.getLimitDetail(limitId);
    }

    @RequestMapping("/limit/update")
    public AuthLimit limitUpdate(@RequestBody AuthLimit limit){
        permService.updateLimit(limit);
        return limit;
    }
    
    @RequestMapping("/limit/remove")
    public void limitRemove(String limitId){
        permService.removeLimit(limitId);
    }

    
    @RequestMapping("/perm/list")
    public List<AuthPermission> perms(){
        return permService.getPerms();
    }
    
    @RequestMapping("/perm/detail")
    public AuthPermission permDetail(String permId){
        return permService.getPermDetail(permId);
    }
    
    @RequestMapping("/perm/update")
    public AuthPermission permUpdate(@RequestBody AuthPermission perm){
        permService.updatePerm(perm);
        return perm;
    }
    
    @RequestMapping("/perm/remove")
    public void permRemove(String permId){
        permService.removePerm(permId);
    }

    
    @RequestMapping("/group/list")
    public List<AuthGroup> groups(){
        return permService.getGroups();
    }

    
    @RequestMapping("/group/detail")
    public AuthGroup groupDetail(String groupId){
        return permService.getGroupDetail(groupId);
    }
    
    @RequestMapping("/group/update")
    public UserGroupBO groupUpdate(@RequestBody UserGroupBO group){
        permService.updateGroup(group);
        return permService.getGroupDetail(group.getGroupId());
    }
    
    @RequestMapping("/group/remove")
    public void groupRemove(String groupId){
        permService.removeGroup(groupId);
    }

    @RequestMapping("/group/remove/account")
    public AuthGroup groupRemoveAccount(String groupId,String accountId){
        permService.removeAccountFromGroup(groupId,accountId);
        return permService.getGroupDetail(groupId);
    }

    @RequestMapping("/group/add/account")
    public AuthGroup groupAddAccount(String groupId, String accountId){
        permService.addAccountFromGroup(groupId,accountId);
        return permService.getGroupDetail(groupId);
    }
    
    @RequestMapping("/accounts")
    public List<UserAccount> accounts(String keyword){
        return permService.accounts(keyword);
    }
}
