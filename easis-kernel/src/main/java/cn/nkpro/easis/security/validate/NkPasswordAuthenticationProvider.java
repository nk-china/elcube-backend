package cn.nkpro.easis.security.validate;

import cn.nkpro.easis.basic.Constants;
import cn.nkpro.easis.data.redis.RedisSupport;
import cn.nkpro.easis.security.bo.UserDetails;
import cn.nkpro.easis.security.UserAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;

public class NkPasswordAuthenticationProvider implements AuthenticationProvider {

    private UserAccountService userDetailsService;

    private RedisSupport<Object> redisSupport;

    public NkPasswordAuthenticationProvider(UserDetailsService userDetailsService, RedisSupport<Object> redisSupport){
        this.userDetailsService = (UserAccountService) userDetailsService;
        this.redisSupport = redisSupport;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        NkPasswordAuthentication nkAuthentication = (NkPasswordAuthentication) authentication;

        String key = Constants.CACHE_AUTH_ERROR+nkAuthentication.getUsername();

        Integer s = (Integer) redisSupport.get(key);
        if(s!=null){

            if(s>=5){
                // 如果用户仍然继续尝试登陆，那么延长锁定时间 重试6次锁定4小时，7次9小时，8次16小时，9次25小时，10次36小时，以此类推
                long increment = redisSupport.increment(key, 1);
                redisSupport.expire(key, (long) (60 * 60 * Math.pow(increment-4,2)));
                throw new BadCredentialsException("账号已被锁定，请稍后再试");
            }

            // 校验验证码
            Object code = redisSupport.get(nkAuthentication.getVerKey());

            if(code==null)
                throw new BadCredentialsException("验证码已过期");

            if(!Objects.equals(code, nkAuthentication.getVerCode())){
                throw new BadCredentialsException("验证码不正确");
            }
        }

        UserDetails details = (UserDetails) userDetailsService.loadUserByUsername(nkAuthentication.getUsername());

        if(details==null){
            throw new UsernameNotFoundException("账号没有找到");
        }else if(!StringUtils.equals(details.getPassword(),nkAuthentication.getPassword())){
            long increment = redisSupport.increment(key, 1);

            if(increment>=5){
                redisSupport.expire(key, 60 * 60 * increment);
                throw new BadCredentialsException("密码错误次数过多，账号已被锁定，请稍后再试");
            }else{
                redisSupport.expire(key,60 * 5 * increment);
                throw new BadCredentialsException("密码错误");
            }

        }else{
            nkAuthentication.setAuthenticated(true);
            nkAuthentication.setDetails(details);
            redisSupport.delete(key);
            return nkAuthentication;
        }
    }

 
    @Override
    public boolean supports(Class<?> authentication) {
        return (NkPasswordAuthentication.class.isAssignableFrom(authentication));
    }
}