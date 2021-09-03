package cn.nkpro.ts5.security;

import cn.nkpro.ts5.security.bo.UserGroupBO;
import cn.nkpro.ts5.security.mybatis.gen.SysAccount;
import cn.nkpro.ts5.security.mybatis.gen.SysAuthGroup;
import cn.nkpro.ts5.security.mybatis.gen.SysAuthLimit;
import cn.nkpro.ts5.security.mybatis.gen.SysAuthPermission;

import java.util.List;

public interface UserAuthorizationService {



    Integer GROUP_TO_ACCOUNT = 0;
    Integer GROUP_TO_PERM = 1;


    List<NkGrantedAuthority> buildGrantedPerms(String accountId, String partnerId);

    List<SysAuthLimit> getLimits(String[] limitIds);

    SysAuthLimit getLimitDetail(String limitId);

    void updateLimit(SysAuthLimit limit);

    void removeLimit(String limitId);

    List<SysAuthPermission> getPerms();

    SysAuthPermission getPermDetail(String permId);

    void updatePerm(SysAuthPermission perm);

    void removePerm(String permId);

    List<SysAuthGroup> getGroups();

    UserGroupBO getGroupDetail(String groupId);

    void updateGroup(UserGroupBO group);

    void removeGroup(String groupId);

    void removeAccountFromGroup(String groupId, String accountId);

    void addAccountFromGroup(String groupId, String accountId);

    List<SysAccount> accounts(String keyword);
}
