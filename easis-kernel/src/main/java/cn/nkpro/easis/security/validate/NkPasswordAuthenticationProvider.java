package cn.nkpro.easis.security.validate;

import cn.nkpro.easis.security.bo.UserDetails;
import cn.nkpro.easis.security.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class NkPasswordAuthenticationProvider implements AuthenticationProvider {

    private UserAccountService userDetailsService;

    public NkPasswordAuthenticationProvider(UserDetailsService userDetailsService){
        this.userDetailsService = (UserAccountService) userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        NkPasswordAuthentication nkAuthentication = (NkPasswordAuthentication) authentication;

        UserDetails details = (UserDetails) userDetailsService.loadUserByUsername(nkAuthentication.getUsername());

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
        return (NkPasswordAuthentication.class.isAssignableFrom(authentication));
    }
}