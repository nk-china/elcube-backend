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

import cn.nkpro.elcube.security.JwtHelper;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
public class NkTokenAuthenticationFilter extends GenericFilterBean {

    private AuthenticationManager authenticationManager;

    private AuthenticationEntryPoint authenticationEntryPoint;

    public NkTokenAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {


        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {

                String nkApp    = StringUtils.defaultString(obtainParam(request, "NK-App"));
                String tokenStr = obtainParam(request, "NK-Token");

                if (StringUtils.isNoneBlank(nkApp, tokenStr)) {
                    Claims token = JwtHelper.verifyJwt(tokenStr);

                    if(token==null){
                        throw new BadCredentialsException("无效的token");
                    }
                    String username = token.get("username", String.class);
                    String password = token.get("password", String.class);

                    String phone = token.get("phone", String.class);
                    String openId   = token.get("openId", String.class);
                    String appleId  = token.get("appleId", String.class);

                    Authentication responseAuthentication = null;

                    if(StringUtils.isNoneBlank(username, password)){
                        responseAuthentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(username, password)
                        );
                    }else if(!StringUtils.isAllBlank(phone, openId, appleId)){
                        responseAuthentication = authenticationManager.authenticate(
                                new NkAppLoginAuthentication(nkApp, phone, null, openId, appleId)
                        );
                    }

                    try{

                        if (responseAuthentication != null && responseAuthentication.isAuthenticated()) {
                            if(logger.isDebugEnabled())
                                logger.debug("["+responseAuthentication.getPrincipal()+"] successfully authenticated");
                            SecurityContextHolder.getContext().setAuthentication(responseAuthentication);
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
