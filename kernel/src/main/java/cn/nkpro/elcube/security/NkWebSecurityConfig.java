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
package cn.nkpro.elcube.security;

import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.security.validate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.List;

/**
 * Created by bean on 2019/12/30.
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true,prePostEnabled = true)
public class NkWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @SuppressWarnings("all")
    @Autowired
    private UserAccountService userDetailsService;

    @SuppressWarnings("all")
    @Autowired
    private List<NkCodeUserDetailsService> userDetailsServices;

    @SuppressWarnings("all")
    @Autowired
    private RedisSupport<Object> redisSupport;

    private NkAuthenticationEntryPoint nkAuthenticationEntryPoint = new NkAuthenticationEntryPoint();

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new NkUsernamePasswordAuthenticationProvider(userDetailsService,redisSupport));
        auth.authenticationProvider(new NkUsernamePasswordVerCodeAuthenticationProvider(userDetailsService,redisSupport));
        auth.authenticationProvider(new NkCodeAuthenticationProvider(userDetailsServices));
        //auth.authenticationProvider(new NkAppLoginAuthenticationProvider(userDetailsService,redisSupport));
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .headers()
                .frameOptions()
                .sameOrigin()
                .and()
            .addFilterBefore(new NkTokenAuthenticationFilter(authenticationManager(),nkAuthenticationEntryPoint), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new BasicAuthenticationFilter(authenticationManager(),nkAuthenticationEntryPoint), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new NkUsernamePasswordVerCodeAuthenticationFilter(authenticationManager(),nkAuthenticationEntryPoint), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new NkCodeAuthenticationFilter(authenticationManager(),nkAuthenticationEntryPoint), UsernamePasswordAuthenticationFilter.class)
            //.addFilterBefore(new NkAppLoginAuthenticationFilter(authenticationManager(),nkAuthenticationEntryPoint), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(new NkAccessDeniedHandler())
                .authenticationEntryPoint(nkAuthenticationEntryPoint)
                .and()
        ;
    }

    @ConditionalOnMissingBean
    @Bean
    protected UserBusinessAdapter userBusinessAdapter(){
        return new UserBusinessAdapter(){};
    }
}
