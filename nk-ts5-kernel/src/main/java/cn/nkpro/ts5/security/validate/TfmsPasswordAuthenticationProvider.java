package cn.nkpro.ts5.security.validate;

import cn.nkpro.ts5.security.TfmsUserDetails;
import cn.nkpro.ts5.security.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TfmsPasswordAuthenticationProvider implements AuthenticationProvider {

    private UserAccountService userDetailsService;

    public TfmsPasswordAuthenticationProvider(UserDetailsService userDetailsService){
        this.userDetailsService = (UserAccountService) userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        TfmsPasswordAuthentication nkAuthentication = (TfmsPasswordAuthentication) authentication;

        TfmsUserDetails details = (TfmsUserDetails) userDetailsService.loadUserByUsername(nkAuthentication.getUsername());

        if(details==null){
            throw new UsernameNotFoundException("用户没有找到");
        }else if(!StringUtils.equals(details.getPassword(),nkAuthentication.getPassword())){
            throw new BadCredentialsException("密码错误");
        }else{
            nkAuthentication.setAuthenticated(true);
            nkAuthentication.setDetails(details);
            return nkAuthentication;
        }
    }

 
    @Override
    public boolean supports(Class<?> authentication) {
        return (TfmsPasswordAuthentication.class.isAssignableFrom(authentication));
    }
}