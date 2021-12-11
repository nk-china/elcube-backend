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
package cn.nkpro.elcube.platform;


import cn.nkpro.elcube.co.NkComponentException;
import cn.nkpro.elcube.exception.NkAccessDeniedException;
import cn.nkpro.elcube.exception.NkDebugContextNotFoundException;
import cn.nkpro.elcube.exception.NkDefineException;
import cn.nkpro.elcube.exception.abstracts.NkCaution;
import cn.nkpro.elcube.exception.abstracts.NkRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static Map<Class<? extends Throwable>,Integer> codes = new LinkedHashMap<>();

    static {
        // 用户操作错误或警告提示
        codes.put(NkCaution.class,400);
        codes.put(IllegalArgumentException.class,400);

        // 用户未登陆 或 token失效
        codes.put(AuthenticationException.class,401);

        // 没有权限，拒绝访问
        codes.put(AccessDeniedException.class,403);
        codes.put(NkAccessDeniedException.class,403);


        // 调试错误
        codes.put(NkDebugContextNotFoundException.class,701);

        // 系统错误
        codes.put(NkComponentException.class,501);
        codes.put(NkDefineException.class,501);
        codes.put(NkRuntimeException.class,501);
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView errorHandler(HttpServletRequest request,
                                     HttpServletResponse response, Exception ex) {

        String message = ex.getMessage();

        if(StringUtils.isBlank(message)){
            if(ex instanceof NullPointerException){
                message = "空指针";
            }else{
                message = "未知错误";
            }
        }

        log.error(message,ex);

        response.setStatus(500);
        for(Class<?> type : codes.keySet()){
            if(ClassUtils.isAssignable(ex.getClass(),type)){
                response.setStatus(codes.get(type));
                break;
            }
        }

        MappingJackson2JsonView view = new MappingJackson2JsonView();
        Map<String, Object> attributes = new HashMap<>();

        attributes.put("code", response.getStatus());
        attributes.put("msg", message);
        attributes.put("url", request.getRequestURI().substring(request.getContextPath().length()));
        attributes.put("causeStackTrace", ExceptionUtils.getRootCauseStackTrace(ex));

        view.setAttributesMap(attributes);
        ModelAndView mav = new ModelAndView("error");
        mav.setView(view);
        return mav;
    }
}