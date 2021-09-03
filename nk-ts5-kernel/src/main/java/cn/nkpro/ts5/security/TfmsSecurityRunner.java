package cn.nkpro.ts5.security;

import cn.nkpro.ts5.security.validate.TfmsPasswordAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class TfmsSecurityRunner {

    @Autowired
    private UserDetailsService userDetailsService;

    public void runAsUser(String username){

        TfmsPasswordAuthentication authentication = new TfmsPasswordAuthentication(username,null);
        TfmsUserDetails details = (TfmsUserDetails) userDetailsService.loadUserByUsername(authentication.getUsername());

        authentication.setAuthenticated(true);
        authentication.setDetails(details);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
