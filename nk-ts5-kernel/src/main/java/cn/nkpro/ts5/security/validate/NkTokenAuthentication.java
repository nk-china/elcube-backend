package cn.nkpro.ts5.security.validate;

import cn.nkpro.ts5.security.bo.UserDetails;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class NkTokenAuthentication extends AbstractAuthenticationToken {

    private String jwtStr;
    private String username;
    private String password;

    NkTokenAuthentication(String jwtStr) {
        super(null);
        this.jwtStr = jwtStr;
    }

    NkTokenAuthentication(AbstractAuthenticationToken unAuthed, UserDetails details) {
        super(details.getAuthorities());
        BeanUtils.copyProperties(unAuthed,this);
        this.username = details.getUsername();
        this.password = details.getPassword();
    }

    String getJwtStr() {
        return jwtStr;
    }

    @Override
    public String getCredentials() {
        return password;
    }

    @Override
    public String getPrincipal() {
        return username;
    }

    void setPrincipal(String username) {
        this.username = username;
    }
}