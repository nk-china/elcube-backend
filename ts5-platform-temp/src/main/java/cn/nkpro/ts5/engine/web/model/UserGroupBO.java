package cn.nkpro.ts5.engine.web.model;

import cn.nkpro.ts5.config.security.TfmsGrantedAuthority;
import cn.nkpro.ts5.model.mb.gen.SysAccount;
import cn.nkpro.ts5.model.mb.gen.SysAuthGroup;
import cn.nkpro.ts5.model.mb.gen.SysAuthPermission;
import lombok.Data;

import java.util.List;

@Data
public class UserGroupBO extends SysAuthGroup {

    private List<TfmsGrantedAuthority> authorities;

    private List<SysAuthPermission> permissions;

    private List<SysAccount> accounts;
}
