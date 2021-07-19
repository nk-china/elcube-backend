package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.SysAuthGroupBO;
import cn.nkpro.tfms.platform.model.po.SysAccount;
import cn.nkpro.tfms.platform.model.po.SysAuthGroup;
import cn.nkpro.tfms.platform.model.po.SysAuthLimit;
import cn.nkpro.tfms.platform.model.po.SysAuthPermission;
import cn.nkpro.ts5.config.security.TfmsGrantedAuthority;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.List;

public interface TfmsPermService {



    Integer GROUP_TO_ACCOUNT = 0;
    Integer GROUP_TO_PERM = 1;

    //String MODE_ADD =     "NEW";
    String MODE_READ =    "READ";
    String MODE_WRITE =   "WRITE";
    //String MODE_REMOVE =  "REMOVE";


    List<TfmsGrantedAuthority> buildGrantedPerms(String accountId,String partnerId);

    BoolQueryBuilder buildDocFilter(String mode,String docType,String typeKey, boolean ignoreLimit);

    DefDocTypeBO filterDocCards(String mode, DefDocTypeBO runtimeDefined);

    void assertHasDocPerm(String mode, String docType);

    void assertHasDocPerm(String mode, String docId, String docType);

    boolean hasDocPerm(String mode, String docType);

    boolean hasDocPerm(String mode, String docId, String docType);

    List<SysAuthLimit> getLimits(String[] limitIds);

    SysAuthLimit getLimitDetail(String limitId);

    void updateLimit(SysAuthLimit limit);

    void removeLimit(String limitId);

    List<SysAuthPermission> getPerms();

    SysAuthPermission getPermDetail(String permId);

    void updatePerm(SysAuthPermission perm);

    void removePerm(String permId);

    List<SysAuthGroup> getGroups();

    SysAuthGroupBO getGroupDetail(String groupId);

    void updateGroup(SysAuthGroupBO group);

    void removeGroup(String groupId);

    void removeAccountFromGroup(String groupId, String accountId);

    void addAccountFromGroup(String groupId, String accountId);

    List<SysAccount> accounts(String keyword);
}
