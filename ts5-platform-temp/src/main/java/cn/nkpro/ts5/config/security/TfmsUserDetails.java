package cn.nkpro.ts5.config.security;

import cn.nkpro.ts5.model.SystemAccountBO;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by bean on 2019/12/30.
 */
public class TfmsUserDetails extends SystemAccountBO implements UserDetails {

    @Override
    public List<TfmsGrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }
    @Override
    public boolean isEnabled() {
        return getLocked()==null || getLocked()==0;
    }
    @Override
    public boolean isAccountNonLocked() {
        return getLocked()!=null && getLocked()==0;
    }
    @Override
    public boolean isAccountNonExpired() {
        return getValidTo()!=null && DateTimeUtilz.todayShortString().compareTo(getValidTo())>=0;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return getValidFrom()!=null && DateTimeUtilz.todayShortString().compareTo(getValidFrom())<=0;
    }

}
