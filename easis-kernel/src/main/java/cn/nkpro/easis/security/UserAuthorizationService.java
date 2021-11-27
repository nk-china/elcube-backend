package cn.nkpro.easis.security;

import cn.nkpro.easis.platform.gen.UserAccount;
import cn.nkpro.easis.security.bo.GrantedAuthority;
import cn.nkpro.easis.security.bo.UserGroupBO;
import cn.nkpro.easis.security.gen.AuthGroup;
import cn.nkpro.easis.security.gen.AuthLimit;
import cn.nkpro.easis.security.gen.AuthPermission;

import java.util.List;

public interface UserAuthorizationService {



    Integer GROUP_TO_ACCOUNT = 0;
    Integer GROUP_TO_PERM = 1;


    List<GrantedAuthority> buildGrantedPerms(String accountId, String partnerId);

    List<AuthLimit> getLimits(String[] limitIds);

    AuthLimit getLimitDetail(String limitId);

    void updateLimit(AuthLimit limit);

    void removeLimit(String limitId);

    List<AuthPermission> getPerms();

    AuthPermission getPermDetail(String permId);

    void updatePerm(AuthPermission perm);

    void removePerm(String permId);

    List<AuthGroup> getGroups();

    List<UserGroupBO> getGroupBOs();

    UserGroupBO getGroupDetail(String groupId);

    void updateGroup(UserGroupBO group);

    void removeGroup(String groupId);

    void removeAccountFromGroup(String groupId, String accountId);

    void addAccountFromGroup(String groupId, String accountId);

    List<UserAccount> accounts(String keyword);
}
