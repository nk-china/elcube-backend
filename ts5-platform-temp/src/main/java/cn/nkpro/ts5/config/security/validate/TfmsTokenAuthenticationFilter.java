package cn.nkpro.ts5.config.security.validate;

import cn.nkpro.ts5.config.security.TfmsUserDetails;
import cn.nkpro.ts5.supports.defaults.DefaultRedisSupportImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by bean on 2019/12/30.
 */
public class TfmsTokenAuthenticationFilter extends GenericFilterBean {
    @Autowired
    private DefaultRedisSupportImpl redisSupport;

    protected AuthenticationManager authenticationManager;

    private AuthenticationEntryPoint authenticationEntryPoint;

    public TfmsTokenAuthenticationFilter(AuthenticationManager authenticationManager,AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {


        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {

                String tokenStr = obtainParam(request, "NK-Token");
//                String tokenStr=obtainParam(request,redisSupport.get())
                if (StringUtils.isNotBlank(tokenStr)) {

                    TfmsTokenAuthentication nkAuthentication = new TfmsTokenAuthentication(tokenStr);

                    try{
                        Authentication responseAuthentication = authenticationManager.authenticate(nkAuthentication);

                        if (responseAuthentication != null && responseAuthentication.isAuthenticated()) {
                            logger.info("User successfully authenticated");
                            SecurityContextHolder.getContext().setAuthentication(
                                    new TfmsTokenAuthentication(nkAuthentication, (TfmsUserDetails) responseAuthentication.getDetails()));
                        }
                    }catch (AuthenticationException e){
                        this.authenticationEntryPoint.commence((HttpServletRequest)request, (HttpServletResponse)response, e);
                        return;
                    }
                }
            }

            filterChain.doFilter(request, response);
        }
    }

    private String obtainParam(ServletRequest request,String param) {
        String token = ((HttpServletRequest)request).getHeader(param);
        if (Objects.isNull(token)) {
            token = request.getParameter(param);
        }
        return token;
    }
}
