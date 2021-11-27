package cn.nkpro.easis.security.validate;

import cn.nkpro.easis.security.JwtHelper;
import cn.nkpro.easis.security.UserAccountService;
import cn.nkpro.easis.security.bo.UserDetails;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class NkTokenAuthenticationProvider implements AuthenticationProvider {

    private UserAccountService userDetailsService;

    public NkTokenAuthenticationProvider(UserAccountService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        NkTokenAuthentication nkAuthentication = (NkTokenAuthentication) authentication;

        Claims token = JwtHelper.verifyJwt(nkAuthentication.getJwtStr());

        if (token != null) {
            String username = token.get("username", String.class);
            String password = token.get("password", String.class);
            UserDetails user = userDetailsService.loadUserByUsernameFromCache(username);
            if(user==null){
                throw new BadCredentialsException("无效的用户信息");
            }

            if(!StringUtils.equals(password,user.getPassword())){
                throw new BadCredentialsException("无效的用户凭证");
            }

            //验证token逻辑
            nkAuthentication.setPrincipal(user.getUsername());
            nkAuthentication.setAuthenticated(true);
            nkAuthentication.setDetails(user);

            return authentication;
        }else{
            throw new BadCredentialsException("无效的token");
        }
    }

 
    @Override
    public boolean supports(Class<?> authentication) {
        return (NkTokenAuthentication.class.isAssignableFrom(authentication));
    }
}