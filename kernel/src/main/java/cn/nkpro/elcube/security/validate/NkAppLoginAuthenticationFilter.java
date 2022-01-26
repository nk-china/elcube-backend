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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


public class NkAppLoginAuthenticationFilter extends GenericFilterBean  {

    private AuthenticationManager authenticationManager;

    private AuthenticationEntryPoint authenticationEntryPoint;

    public NkAppLoginAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
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
                String phone;
                String verCode = null;
                String openId;
                String appleId;
                if (StringUtils.isNoneBlank(nkApp) && NkAppSource.valueOf(nkApp)!=NkAppSource.elcube && !((HttpServletRequest) request).getRequestURI().contains("/app/bind")) {
                    if(StringUtils.isNoneBlank(tokenStr)){
                        // token验证
                        Claims token = JwtHelper.verifyJwt(tokenStr);

                        if(null==token){
                            throw new BadCredentialsException("无效的token");
                        }
                        phone = token.get("phone", String.class);
                        //verCode = token.get("verCode", String.class);
                        openId   = token.get("openId", String.class);
                        appleId  = token.get("appleId", String.class);

                    }else{
                        // 首次验证
                        phone = StringUtils.defaultString(obtainParam(request, "phone"));
                        verCode = StringUtils.defaultString(obtainParam(request, "verCode"));
                        openId   = StringUtils.defaultString(obtainParam(request, "openId"));
                        appleId  = StringUtils.defaultString(obtainParam(request, "appleId"));
                    }
                    try{
                        Authentication responseAuthentication = authenticationManager.authenticate(
                                new NkAppLoginAuthentication(nkApp, phone, verCode, openId, appleId)
                        );

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
        String value = ((HttpServletRequest)request).getHeader(param);
        if (Objects.isNull(value)) {
            value = request.getParameter(param);
        }
        return value;
    }

}
