package cn.nkpro.ts5.config.security.validate;

import cn.nkpro.ts5.config.security.TfmsUserDetails;
import cn.nkpro.ts5.config.security.TfmsUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TfmsSecurityRunner {

    @Autowired
    private TfmsUserDetailsService userDetailsService;

    public void runAsUser(String username){

        TfmsPasswordAuthentication authentication = new TfmsPasswordAuthentication(username,null);
        TfmsUserDetails details = userDetailsService.loadUserByUsername(authentication.getUsername());

        authentication.setAuthenticated(true);
        authentication.setDetails(details);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
