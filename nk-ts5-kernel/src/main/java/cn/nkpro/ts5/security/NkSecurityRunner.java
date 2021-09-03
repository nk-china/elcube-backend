package cn.nkpro.ts5.security;

import cn.nkpro.ts5.security.bo.UserDetails;
import cn.nkpro.ts5.security.validate.NkPasswordAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class NkSecurityRunner {

    @Autowired
    private UserDetailsService userDetailsService;

    public void runAsUser(String username){

        NkPasswordAuthentication authentication = new NkPasswordAuthentication(username,null);
        UserDetails details = (UserDetails) userDetailsService.loadUserByUsername(authentication.getUsername());

        authentication.setAuthenticated(true);
        authentication.setDetails(details);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
