package cn.nkpro.easis.security.bo;

import cn.nkpro.easis.platform.gen.UserAccount;
import cn.nkpro.easis.security.gen.AuthGroup;
import cn.nkpro.easis.security.gen.AuthPermission;
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
