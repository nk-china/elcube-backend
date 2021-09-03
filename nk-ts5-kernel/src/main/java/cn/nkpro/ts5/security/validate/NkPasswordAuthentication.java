package cn.nkpro.ts5.security.validate;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class NkPasswordAuthentication extends AbstractAuthenticationToken {

    private String username;

    private String password;

    public NkPasswordAuthentication(String username, String password) {
        super(null);
        this.username = username;
        this.password = password;
    }

    @Override
    public String getCredentials() {
        return password;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPrincipal() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}