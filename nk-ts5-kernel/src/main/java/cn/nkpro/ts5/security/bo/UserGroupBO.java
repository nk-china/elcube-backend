package cn.nkpro.ts5.security.bo;

import cn.nkpro.ts5.platform.gen.UserAccount;
import cn.nkpro.ts5.security.gen.AuthGroup;
import cn.nkpro.ts5.security.gen.AuthPermission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserGroupBO extends AuthGroup {

    private List<GrantedAuthority> authorities;

    private List<AuthPermission> permissions;

    private List<UserAccount> accounts;
}
