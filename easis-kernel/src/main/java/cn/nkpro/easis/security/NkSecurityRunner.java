package cn.nkpro.easis.security;

import cn.nkpro.easis.security.bo.GrantedAuthority;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class NkSecurityRunner {

    @Qualifier("NkSysAccountService")
    @Autowired@SuppressWarnings("all")
    private UserDetailsService userDetailsService;

    public void runAsUser(String username,Function function){
        UserDetails details = userDetailsService.loadUserByUsername(username);

        GrantedAuthority authority = new GrantedAuthority();
        authority.setAuthority("*:*");

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(authority)
        );
        authentication.setDetails(details);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try{
            function.apply();
        }finally {
            SecurityContextHolder.clearContext();
        }
    }

    @FunctionalInterface
    public interface Function{
        void apply();
    }
}
