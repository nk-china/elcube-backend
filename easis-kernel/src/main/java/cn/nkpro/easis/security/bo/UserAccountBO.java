package cn.nkpro.easis.security.bo;

import cn.nkpro.easis.platform.gen.UserAccount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by bean on 2019/12/30.
 */
public class UserAccountBO extends UserAccount {

    @Getter@Setter
    private List<GrantedAuthority> authorities;
}
