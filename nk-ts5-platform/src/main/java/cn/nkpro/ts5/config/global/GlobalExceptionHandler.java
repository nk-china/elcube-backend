package cn.nkpro.ts5.config.global;


import cn.nkpro.ts5.exception.TfmsAccessDeniedException;
import cn.nkpro.ts5.exception.TfmsCaution;
import cn.nkpro.ts5.exception.TfmsComponentException;
import cn.nkpro.ts5.exception.TfmsException;
import lombok.extern.slf4j.Slf4j;
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

    @ExceptionHandler(value = Exception.class)
    public ModelAndView errorHandler(HttpServletRequest request,
                                     HttpServletResponse response, Exception ex) {

        log.error(ex.getMessage(),ex);

        MappingJackson2JsonView view = new MappingJackson2JsonView();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("msg", ex.getMessage());
        attributes.put("url", request.getRequestURI().substring(request.getContextPath().length()));


        if(ex instanceof TfmsComponentException){
            if(ex.getCause() instanceof TfmsCaution){
                response.setStatus(400);
                attributes.put("msg", ex.getCause().getMessage());
            }else{
                response.setStatus(501);
            }
        }else

        if(ex instanceof TfmsCaution){
            response.setStatus(400);
        }else

        if(ex instanceof AuthenticationException){
            response.setStatus(401);
        }else

        if(ex instanceof TfmsAccessDeniedException){
            response.setStatus(403);
        }else

        if(ex instanceof TfmsException){
            response.setStatus(501);
        }else

        {
            response.setStatus(500);
        }

        attributes.put("code", response.getStatus());
        view.setAttributesMap(attributes);
        ModelAndView mav = new ModelAndView("error");
        mav.setView(view);
        return mav;
    }
}