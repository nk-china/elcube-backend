package cn.nkpro.ts5.exception;


import cn.nkpro.ts5.exception.abstracts.TfmsCaution;
import cn.nkpro.ts5.exception.abstracts.TfmsRuntimeException;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static Map<Class<? extends Throwable>,Integer> codes = new HashMap<>();

    static {
        // 用户操作错误或警告提示
        codes.put(TfmsCaution.class,400);
        codes.put(IllegalArgumentException.class,400);

        // 用户未登陆 或 token失效
        codes.put(AccessDeniedException.class,401);
        codes.put(AuthenticationException.class,401);

        // 没有权限，拒绝访问
        codes.put(TfmsAccessDeniedException.class,403);

        // 系统错误
        codes.put(TfmsComponentException.class,501);
        codes.put(TfmsDefineException.class,501);
        codes.put(TfmsRuntimeException.class,501);
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

        response.setStatus(codes.getOrDefault(ex.getClass(),500));

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