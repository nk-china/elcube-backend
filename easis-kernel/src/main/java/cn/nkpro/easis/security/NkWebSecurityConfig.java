package cn.nkpro.easis.security;

import cn.nkpro.easis.data.redis.RedisSupport;
import cn.nkpro.easis.security.validate.NkPasswordAuthenticationFilter;
import cn.nkpro.easis.security.validate.NkPasswordAuthenticationProvider;
import cn.nkpro.easis.security.validate.NkTokenAuthenticationFilter;
import cn.nkpro.easis.security.validate.NkTokenAuthenticationProvider;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    private RedisSupport<Object> redisSupport;

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
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
//            .authorizeRequests()
//                .antMatchers(
//                        "/wsdoc",
//                        "/wsdoc/**",
//                        "/public/**",
//                        "/file/d/**",
//                        "/def/deploy/d/**"
//                    ).permitAll()
//                .antMatchers("/authentication/token")
//                    .hasAnyAuthority("*:*","SYS:LOGIN")
//                .anyRequest()
//                    .authenticated()
//                .and()
            .exceptionHandling()
                .accessDeniedHandler(new NkAccessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
            .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(passwordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;

    }

    @Override
    protected void  configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(tokenAuthenticationProvider());
        auth.authenticationProvider(passwordAuthenticationProvider());
    }

    @ConditionalOnMissingBean
    @Bean
    protected NkPasswordAuthenticationFilter passwordAuthenticationFilter() throws Exception {
        return new NkPasswordAuthenticationFilter(authenticationManager(),authenticationEntryPoint());
    }

    @ConditionalOnMissingBean
    @Bean
    protected NkTokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
        return new NkTokenAuthenticationFilter(authenticationManager(),authenticationEntryPoint());
    }


    @ConditionalOnMissingBean
    @Bean
    protected NkTokenAuthenticationProvider tokenAuthenticationProvider() {
        return new NkTokenAuthenticationProvider(userDetailsService);
    }

    @ConditionalOnMissingBean
    @Bean
    protected NkPasswordAuthenticationProvider passwordAuthenticationProvider() {
        return new NkPasswordAuthenticationProvider(userDetailsService,redisSupport);
    }

    @ConditionalOnMissingBean
    @Bean
    protected NkAuthenticationEntryPoint authenticationEntryPoint(){
        return new NkAuthenticationEntryPoint();
    }

    @ConditionalOnMissingBean
    @Bean
    protected UserBusinessAdapter userBusinessAdapter(){
        return new UserBusinessAdapter(){};
    }
}
