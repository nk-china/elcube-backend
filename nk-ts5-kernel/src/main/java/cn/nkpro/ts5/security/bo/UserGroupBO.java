package cn.nkpro.ts5.security.bo;

import cn.nkpro.ts5.security.gen.SysAccount;
import cn.nkpro.ts5.security.gen.SysAuthGroup;
import cn.nkpro.ts5.security.gen.SysAuthPermission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserGroupBO extends SysAuthGroup {

    private List<GrantedAuthority> authorities;

    private List<SysAuthPermission> permissions;

    private List<SysAccount> accounts;
}
