package cn.nkpro.ts5.engine.doc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bean on 2019/12/30.
 */
@Configuration
public class NKDocEngineMvcConfigurer implements WebMvcConfigurer {

    private static Map<String, ApplicationContext> debugContext = new ConcurrentHashMap<>();
    private final static ThreadLocal<String>       localDebugId      = new ThreadLocal<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DebugHandlerInterceptor());
    }

    class DebugHandlerInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String debugId = request.getHeader("NK-Debug");
            if(StringUtils.isNotBlank(debugId)){
                localDebugId.set(debugId);
                if(debugContext.get(debugId)==null){
                    GenericApplicationContext context = new GenericApplicationContext(applicationContext);
                    context.refresh();
                    debugContext.put(debugId,context);
                }
            }
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            localDebugId.remove();
        }
    }
}
