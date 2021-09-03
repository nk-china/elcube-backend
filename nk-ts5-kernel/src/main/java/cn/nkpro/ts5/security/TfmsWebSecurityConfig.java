package cn.nkpro.ts5.security;

import cn.nkpro.ts5.security.validate.TfmsPasswordAuthenticationProvider;
import cn.nkpro.ts5.security.validate.TfmsTokenAuthenticationFilter;
import cn.nkpro.ts5.security.validate.TfmsPasswordAuthenticationFilter;
import cn.nkpro.ts5.security.validate.TfmsTokenAuthenticationProvider;
import org.mybatis.spring.annotation.MapperScan;
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

/**
 * Created by bean on 2019/12/30.
 */
@MapperScan(basePackages = {"cn.nkpro.ts5.security.mybatis"})
@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true,prePostEnabled = true)
public class TfmsWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtHelper jwt;

    @Autowired
    private UserAccountService userDetailsService;

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
                .accessDeniedHandler(new TfmsAccessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
            .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(passwordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;

    }

    @Override
    protected void  configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(tokenAuthenticationProvider());
        auth.authenticationProvider(passwordAuthenticationProvider());
    }

    @ConditionalOnMissingBean
    @Bean
    protected TfmsPasswordAuthenticationFilter passwordAuthenticationFilter() throws Exception {
        return new TfmsPasswordAuthenticationFilter(authenticationManager(),authenticationEntryPoint());
    }

    @ConditionalOnMissingBean
    @Bean
    protected TfmsTokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
        return new TfmsTokenAuthenticationFilter(authenticationManager(),authenticationEntryPoint());
    }


    @ConditionalOnMissingBean
    @Bean
    protected TfmsTokenAuthenticationProvider tokenAuthenticationProvider() {
        return new TfmsTokenAuthenticationProvider(jwt, userDetailsService);
    }

    @ConditionalOnMissingBean
    @Bean
    protected TfmsPasswordAuthenticationProvider passwordAuthenticationProvider() {
        return new TfmsPasswordAuthenticationProvider((UserDetailsService) userDetailsService);
    }

    @ConditionalOnMissingBean
    @Bean
    protected TfmsAuthenticationEntryPoint authenticationEntryPoint(){
        return new TfmsAuthenticationEntryPoint();
    }

    @ConditionalOnMissingBean
    @Bean
    protected UserBusinessAdapter userBusinessAdapter(){
        return new UserBusinessAdapter(){};
    }
}
