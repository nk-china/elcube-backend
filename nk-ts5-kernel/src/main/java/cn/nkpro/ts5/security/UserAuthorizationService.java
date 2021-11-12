package cn.nkpro.ts5.security;

import cn.nkpro.ts5.platform.gen.UserAccount;
import cn.nkpro.ts5.security.bo.GrantedAuthority;
import cn.nkpro.ts5.security.bo.UserGroupBO;
import cn.nkpro.ts5.security.gen.AuthGroup;
import cn.nkpro.ts5.security.gen.AuthLimit;
import cn.nkpro.ts5.security.gen.AuthPermission;

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

    UserGroupBO getGroupDetail(String groupId);

    void updateGroup(UserGroupBO group);

    void removeGroup(String groupId);

    void removeAccountFromGroup(String groupId, String accountId);

    void addAccountFromGroup(String groupId, String accountId);

    List<UserAccount> accounts(String keyword);
}
