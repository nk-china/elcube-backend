package cn.nkpro.ts5.config.security;

import cn.nkpro.ts5.engine.web.impl.UserAccountServiceImpl;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by bean on 2019/12/30.
 */
@Component
public class TfmsUserDetailsService extends UserAccountServiceImpl implements UserDetailsService {
    @Override
    public TfmsUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(super.getAccount(username,true))
                .map(account->BeanUtilz.copyFromObject(account,TfmsUserDetails.class))
                .orElse(null);
    }
    public TfmsUserDetails loadUserByUsernameFromCache(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(super.getAccount(username,false))
                .map(account->BeanUtilz.copyFromObject(account,TfmsUserDetails.class))
                .orElse(null);
    }
}
