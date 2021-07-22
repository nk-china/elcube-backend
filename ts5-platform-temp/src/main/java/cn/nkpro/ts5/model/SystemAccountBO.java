package cn.nkpro.ts5.model;

import cn.nkpro.ts5.config.security.TfmsGrantedAuthority;
import cn.nkpro.ts5.model.mb.gen.SysAccount;
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
