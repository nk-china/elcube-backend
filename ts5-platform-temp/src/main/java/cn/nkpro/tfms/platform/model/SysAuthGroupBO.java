package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.SysAccount;
import cn.nkpro.tfms.platform.model.po.SysAuthGroup;
import cn.nkpro.tfms.platform.model.po.SysAuthPermission;
import cn.nkpro.ts5.config.security.TfmsGrantedAuthority;
import lombok.Data;

import java.util.List;

@Data
public class SysAuthGroupBO extends SysAuthGroup {

    private List<TfmsGrantedAuthority> authorities;

    private List<SysAuthPermission> permissions;

    private List<SysAccount> accounts;
}
