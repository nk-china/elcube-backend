package cn.nkpro.elcube.security;

import cn.nkpro.elcube.security.bo.UserDetails;

public interface NkCodeUserDetailsService {
    boolean supports(String type);
    UserDetails loadUser(String code,String secret);
}
