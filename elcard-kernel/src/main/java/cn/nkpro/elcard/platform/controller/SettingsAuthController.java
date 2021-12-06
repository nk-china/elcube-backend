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

import cn.nkpro.elcard.basic.PageList;
import cn.nkpro.elcard.platform.gen.UserAccount;
import cn.nkpro.elcard.security.UserAccountService;
import cn.nkpro.elcard.security.UserAuthorizationService;
import cn.nkpro.elcard.security.bo.UserAccountBO;
import cn.nkpro.elcard.security.bo.UserGroupBO;
import cn.nkpro.elcard.security.gen.AuthGroup;
import cn.nkpro.elcard.security.gen.AuthLimit;
import cn.nkpro.elcard.security.gen.AuthPermission;
import cn.nkpro.elcard.annotation.NkNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NkNote("12.权限设置")
@RestController
@RequestMapping("/settings/auth")
@PreAuthorize("hasAnyAuthority('*:*','SETTINGS:*','SETTINGS:AUTH')")
public class SettingsAuthController {

    @Autowired@SuppressWarnings("all")
    private UserAuthorizationService permService;
    @Autowired@SuppressWarnings("all")
    private UserAccountService accountService;

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

    @RequestMapping("/accounts/list")
    public PageList<UserAccount> accountsList(
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "rows", required = false, defaultValue = "10") Integer rows,
            @RequestParam(value = "orderField", required = false) String orderField,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword){
        return accountService.accountsPage(from, rows, orderField, order, keyword);
    }

    @RequestMapping("/accounts/detail")
    public UserAccountBO accountsDetail(@RequestParam(value = "username") String username){
        UserAccountBO account = accountService.getAccount(username, false);
        account.setPassword(null);
        return account;
    }

    @RequestMapping("/accounts/update")
    public UserAccountBO accountsUpdate(@RequestBody UserAccountBO user){
        return accountService.update(user);
    }

    @RequestMapping("/accounts/clearLoginLock")
    public void clearLoginLock(@RequestBody UserAccountBO user){
        accountService.clearLoginLock(user);
    }
    
    @RequestMapping("/accounts")
    public List<UserAccount> accounts(String keyword){
        return permService.accounts(keyword);
    }
}
