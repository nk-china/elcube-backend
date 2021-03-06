/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.security.validate;

import cn.nkpro.elcube.basic.Constants;
import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.security.UserAccountService;
import cn.nkpro.elcube.security.bo.UserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;

public class NkUsernamePasswordVerCodeAuthenticationProvider implements AuthenticationProvider {

    private UserAccountService userDetailsService;

    private RedisSupport<Object> redisSupport;

    public NkUsernamePasswordVerCodeAuthenticationProvider(UserDetailsService userDetailsService, RedisSupport<Object> redisSupport){
        this.userDetailsService = (UserAccountService) userDetailsService;
        this.redisSupport = redisSupport;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        NkUsernamePasswordVerCodeAuthentication nkAuthentication = (NkUsernamePasswordVerCodeAuthentication) authentication;

        String key = Constants.CACHE_AUTH_ERROR+nkAuthentication.getUsername();

        Integer s = (Integer) redisSupport.get(key);
        if(s!=null){

            if(s>=5){
                // ??????????????????????????????????????????????????????????????? ??????6?????????4?????????7???9?????????8???16?????????9???25?????????10???36?????????????????????
                long increment = redisSupport.increment(key, 1);
                long hour = (long) Math.pow(increment-4,2);
                redisSupport.expire(key, 60 * 60 * hour);
                throw new BadCredentialsException("????????????????????????"+hour+"???????????????");
            }

            // ???????????????
            Object code = redisSupport.get(nkAuthentication.getVerKey());

            if(code==null)
                throw new BadCredentialsException("??????????????????");

            if(!Objects.equals(code, nkAuthentication.getVerCode())){
                throw new BadCredentialsException("??????????????????");
            }
        }
        // ???????????????
        Object code = redisSupport.get(nkAuthentication.getVerKey());
        if(code!=null && !Objects.equals(code, nkAuthentication.getVerCode())){
            throw new BadCredentialsException("??????????????????");
        }

        UserDetails details = (UserDetails) userDetailsService.loadUserByUsername(nkAuthentication.getUsername());

        if(details==null){
            throw new UsernameNotFoundException("??????????????????");
        }
        if(details.getLocked()!=null && details.getLocked()==1){
            throw new BadCredentialsException("???????????????");
        }

        if(!StringUtils.equals(details.getPassword(),nkAuthentication.getPassword())){
            long increment = redisSupport.increment(key, 1);

            if(increment>=5){
                redisSupport.expire(key, 60 * 60);
                throw new BadCredentialsException("???????????????????????????????????????????????????1???????????????");
            }else{
                redisSupport.expire(key,60 * 5 * increment);
                throw new BadCredentialsException("????????????");
            }

        }

        redisSupport.delete(key);

        AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                nkAuthentication.getPrincipal(),
                nkAuthentication.getCredentials(),
                details.getAuthorities());
        auth.setDetails(details);
        return auth;
    }

 
    @Override
    public boolean supports(Class<?> authentication) {
        return (NkUsernamePasswordVerCodeAuthentication.class.isAssignableFrom(authentication));
    }
}