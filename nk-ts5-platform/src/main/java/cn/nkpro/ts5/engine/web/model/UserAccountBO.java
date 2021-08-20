package cn.nkpro.ts5.engine.web.model;

import cn.nkpro.ts5.config.security.NkGrantedAuthority;
import cn.nkpro.ts5.orm.mb.gen.SysAccount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by bean on 2019/12/30.
 */
public class UserAccountBO extends SysAccount {

    @Getter@Setter
    private List<NkGrantedAuthority> authorities;
}
