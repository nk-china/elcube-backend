/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.security.validate;

import cn.nkpro.elcard.basic.Constants;
import cn.nkpro.elcard.data.redis.RedisSupport;
import cn.nkpro.elcard.security.UserAccountService;
import cn.nkpro.elcard.security.bo.UserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class NkUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private UserAccountService userDetailsService;

    private RedisSupport<Object> redisSupport;

    public NkUsernamePasswordAuthenticationProvider(UserAccountService userDetailsService, RedisSupport<Object> redisSupport){
        this.userDetailsService = userDetailsService;
        this.redisSupport = redisSupport;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UsernamePasswordAuthenticationToken passwordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        String key = Constants.CACHE_AUTH_ERROR+ passwordAuthenticationToken.getPrincipal();

        Integer s = (Integer) redisSupport.get(key);
        if(s!=null){
            if(s>=5){
                long hour = redisSupport.getExpire(key);
                throw new BadCredentialsException("账号已被锁定，请"+(hour/60/60+1)+"小时后再试");
            }
        }

        UserDetails details = userDetailsService.loadUserByUsernameFromCache((String) passwordAuthenticationToken.getPrincipal());
        if(details==null){
            throw new BadCredentialsException("无效的账号信息");
        }
        if(details.getLocked()!=null && details.getLocked()==1){
            throw new BadCredentialsException("账号已禁用");
        }

        if(!StringUtils.equals((CharSequence) passwordAuthenticationToken.getCredentials(),details.getPassword())){
            long increment = redisSupport.increment(key, 1);
            if(increment>=5){
                redisSupport.expire(key, 60 * 60);
                throw new BadCredentialsException("密码错误次数过多，账号已被锁定，请1小时后再试");
            }else{
                redisSupport.expire(key,60 * 5 * increment);
                throw new BadCredentialsException("无效的用户凭证");
            }
        }

        passwordAuthenticationToken =  new UsernamePasswordAuthenticationToken(
                passwordAuthenticationToken.getPrincipal(),
                passwordAuthenticationToken.getCredentials(),
                details.getAuthorities()
        );
        passwordAuthenticationToken.setDetails(details);
        return passwordAuthenticationToken;
    }

 
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}