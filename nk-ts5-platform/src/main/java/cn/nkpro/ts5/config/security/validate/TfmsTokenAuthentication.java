package cn.nkpro.ts5.config.security.validate;

import cn.nkpro.ts5.config.security.TfmsUserDetails;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class TfmsTokenAuthentication extends AbstractAuthenticationToken {

    private String jwtStr;
    private String username;
    private String password;

    TfmsTokenAuthentication(String jwtStr) {
        super(null);
        this.jwtStr = jwtStr;
    }

    TfmsTokenAuthentication(AbstractAuthenticationToken unAuthed, TfmsUserDetails details) {
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