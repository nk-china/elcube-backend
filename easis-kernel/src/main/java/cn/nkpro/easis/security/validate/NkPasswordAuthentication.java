package cn.nkpro.easis.security.validate;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class NkPasswordAuthentication extends AbstractAuthenticationToken {

    @Getter
    private String username;
    @Getter
    private String password;
    @Getter
    private String verCode;
    @Getter
    private String verKey;

    public NkPasswordAuthentication(String username, String password, String verKey, String verCode) {
        super(null);
        this.username = username;
        this.password = password;
        this.verKey = verKey;
        this.verCode = verCode;
    }

    @Override
    public String getCredentials() {
        return password;
    }

//    protected void setUsername(String username) {
//        this.username = username;
//    }

    @Override
    public String getPrincipal() {
        return username;
    }
}