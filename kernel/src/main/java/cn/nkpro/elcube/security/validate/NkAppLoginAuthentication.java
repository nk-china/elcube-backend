///*
// * This file is part of ELCube.
// *
// * ELCube is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Affero General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * ELCube is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Affero General Public License for more details.
// *
// * You should have received a copy of the GNU Affero General Public License
// * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
// */
//package cn.nkpro.elcube.security.validate;
//
//import cn.nkpro.elcube.security.bo.AuthMobileTerminal;
//import com.alibaba.fastjson.JSONObject;
//import lombok.Getter;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//
//import java.util.Collection;
//
//
//public class NkAppLoginAuthentication extends AbstractAuthenticationToken {
//
//    private final Object principal;
//
//    @Getter
//    private String nkAppSource;
//    @Getter
//    private String phone;
//    @Getter
//    private String verCode;
//    @Getter
//    private String openId;
//    @Getter
//    private String appleId;
//
//    public NkAppLoginAuthentication(String nkAppSource, String phone, String verCode, String openId, String appleId) {
//        super(null);
//        this.nkAppSource = nkAppSource;
//        this.phone = phone;
//        this.verCode = verCode;
//        this.openId = openId;
//        this.appleId = appleId;
//        AuthMobileTerminal authMobileTerminal = new AuthMobileTerminal();
//        authMobileTerminal.setPhone(phone);
//        authMobileTerminal.setOpenId(openId);
//        authMobileTerminal.setAppleId(appleId);
//        this.principal = JSONObject.toJSONString(authMobileTerminal);
//    }
//
//    public NkAppLoginAuthentication(Object principal,
//                                          Collection<? extends GrantedAuthority> authorities) {
//        super(authorities);
//        this.principal = principal;
//        // must use super, as we override
//        super.setAuthenticated(true);
//    }
//
//    @Override
//    public void setAuthenticated(boolean authenticated) {
//        if (authenticated) {
//            throw new IllegalArgumentException(
//                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
//        }
//        super.setAuthenticated(false);
//    }
//
//    @Override
//    public Object getCredentials() {
//        return null;
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return this.principal;
//    }
//
//    @Override
//    public void eraseCredentials() {
//        super.eraseCredentials();
//    }
//}
