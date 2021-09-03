package cn.nkpro.ts5.security.bo;

import cn.nkpro.ts5.security.NkGrantedAuthority;
import cn.nkpro.ts5.security.mybatis.gen.SysAccount;
import cn.nkpro.ts5.security.mybatis.gen.SysAuthGroup;
import cn.nkpro.ts5.security.mybatis.gen.SysAuthPermission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserGroupBO extends SysAuthGroup {

    private List<NkGrantedAuthority> authorities;

    private List<SysAuthPermission> permissions;

    private List<SysAccount> accounts;
}
