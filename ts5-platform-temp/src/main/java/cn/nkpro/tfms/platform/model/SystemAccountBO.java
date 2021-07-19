package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.SysAccount;
import cn.nkpro.ts5.config.security.TfmsGrantedAuthority;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by bean on 2019/12/30.
 */
public class SystemAccountBO extends SysAccount {

    @Getter@Setter
    private List<TfmsGrantedAuthority> authorities;
}
