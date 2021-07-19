package cn.nkpro.ts5.config.security.validate;

import cn.nkpro.ts5.config.security.TfmsUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
public class TfmsPasswordAuthenticationFilter extends GenericFilterBean {

    private AuthenticationManager authenticationManager;

    private AuthenticationEntryPoint authenticationEntryPoint;

    public TfmsPasswordAuthenticationFilter(AuthenticationManager authenticationManager,AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {

                String username = StringUtils.defaultString(obtainParam(request, "username"));
                String password = StringUtils.defaultString(obtainParam(request, "password"));

                if (StringUtils.isNoneBlank(username, password)) {
                    TfmsPasswordAuthentication nkAuthentication = new TfmsPasswordAuthentication(username, password);
                    try {
                        Authentication responseAuthentication = authenticationManager.authenticate(nkAuthentication);
                        if (responseAuthentication != null) {
                            if (responseAuthentication.isAuthenticated()) {
                                SecurityContextHolder.getContext().setAuthentication(
                                        new TfmsTokenAuthentication(nkAuthentication, (TfmsUserDetails) responseAuthentication.getDetails()));
                            }
                        }
                    }catch (AuthenticationException e){
                        this.authenticationEntryPoint.commence((HttpServletRequest)request, (HttpServletResponse)response, e);
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String obtainParam(ServletRequest request,String param) {
        String token = ((HttpServletRequest)request).getHeader(param);
        if (Objects.isNull(token)) {
            token = request.getParameter(param);
        }
        return token;
    }
}
