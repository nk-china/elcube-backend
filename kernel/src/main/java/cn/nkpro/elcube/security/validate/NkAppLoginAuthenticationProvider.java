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
//import cn.nkpro.elcube.data.redis.RedisSupport;
//import cn.nkpro.elcube.security.UserAccountService;
//import cn.nkpro.elcube.security.bo.AuthMobileTerminal;
//import cn.nkpro.elcube.security.bo.UserDetails;
//import com.alibaba.fastjson.JSONObject;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.util.Objects;
//
//public class NkAppLoginAuthenticationProvider implements AuthenticationProvider {
//    private UserAccountService userDetailsService;
//
//    private RedisSupport<Object> redisSupport;
//
//    public NkAppLoginAuthenticationProvider(UserDetailsService userDetailsService, RedisSupport<Object> redisSupport){
//        this.userDetailsService = (UserAccountService) userDetailsService;
//        this.redisSupport = redisSupport;
//    }
//
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        NkAppLoginAuthentication nkAuthentication = (NkAppLoginAuthentication) authentication;
//        UserDetails details;
//        switch (NkAppSource.valueOf(nkAuthentication.getNkAppSource())){
//            case weChat:
//                if(StringUtils.isNoneBlank(nkAuthentication.getOpenId())){
//                    // todo 校验openId合法性
//
//                }else if(StringUtils.isNoneBlank(nkAuthentication.getPhone())){
//                    // 校验验证码
//                    /*Object code = redisSupport.get(nkAuthentication.getPhone());
//
//                    if(null==code)
//                        throw new BadCredentialsException("验证码已过期");
//
//                    if(!Objects.equals(code, nkAuthentication.getVerCode())){
//                        throw new BadCredentialsException("验证码不正确");
//                    }*/
//
//                }else{
//                    throw new BadCredentialsException("openId为空");
//                }
//
//                break;
//            case ios:
//                break;
//            case android:
//                break;
//            default:
//                return null;
//        }
//
//        // 根据手机号、openId、appleId 获取用户
//        details = userDetailsService.getAccountByMobileTerminal(nkAuthentication.getPhone(), nkAuthentication.getOpenId(), nkAuthentication.getAppleId());
//
//        if(null==details){
//            throw new UsernameNotFoundException("token未绑定");
//        }
//        if(null!=details.getLocked() && details.getLocked()==1){
//            throw new BadCredentialsException("账号已禁用");
//        }
//
//        AuthMobileTerminal authMobileTerminal = new AuthMobileTerminal();
//        authMobileTerminal.setPhone(nkAuthentication.getPhone());
//        authMobileTerminal.setOpenId(nkAuthentication.getOpenId());
//        authMobileTerminal.setAppleId(nkAuthentication.getAppleId());
//
//        AbstractAuthenticationToken auth = new NkAppLoginAuthentication(
//                JSONObject.toJSONString(authMobileTerminal) ,details.getAuthorities());
//        auth.setDetails(details);
//        return auth;
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return (NkAppLoginAuthentication.class.isAssignableFrom(authentication));
//    }
//
//}
