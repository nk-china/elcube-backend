package cn.nkpro.ts5.security.validate;

import cn.nkpro.ts5.security.JwtHelper;
import cn.nkpro.ts5.security.TfmsUserDetails;
import cn.nkpro.ts5.security.UserAccountService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class TfmsTokenAuthenticationProvider implements AuthenticationProvider {

    private JwtHelper jwt;

    private UserAccountService userDetailsService;

    public TfmsTokenAuthenticationProvider(JwtHelper jwt, UserAccountService userDetailsService){
        this.jwt = jwt;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        TfmsTokenAuthentication nkAuthentication = (TfmsTokenAuthentication) authentication;

        Claims token = jwt.verifyJwt(nkAuthentication.getJwtStr());

        if (token != null) {
            String username = token.get("username", String.class);
            String password = token.get("password", String.class);
            TfmsUserDetails user = userDetailsService.loadUserByUsernameFromCache(username);
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
        return (TfmsTokenAuthentication.class.isAssignableFrom(authentication));
    }
}