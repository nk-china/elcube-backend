package cn.nkpro.elcube.security.impl;

import cn.nkpro.elcube.platform.gen.UserAccountSecret;
import cn.nkpro.elcube.security.NkCodeUserDetailsService;
import cn.nkpro.elcube.security.UserAccountService;
import cn.nkpro.elcube.security.bo.UserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class NKAppKeyUserDetailsServiceImpl implements NkCodeUserDetailsService {

    @SuppressWarnings("all")
    @Autowired
    private UserAccountService accountService;

    @Override
    public boolean supports(String type) {
        return StringUtils.equalsIgnoreCase(type,"appKey");
    }

    @Override
    public UserDetails loadUser(String code,String secret) throws UsernameNotFoundException {
        UserAccountSecret accountSecret = accountService.getAccountSecretByCode(code);

        if(!StringUtils.equals(accountSecret.getSecret(),secret)){
            throw new BadCredentialsException("密钥错误");
        }

        return accountService.loadUserById(accountSecret.getAccountId());
    }
}
