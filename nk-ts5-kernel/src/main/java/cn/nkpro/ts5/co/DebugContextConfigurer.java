package cn.nkpro.ts5.co;

import cn.nkpro.ts5.data.redis.RedisSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by bean on 2019/12/30.
 */
@Configuration
public class DebugContextConfigurer implements WebMvcConfigurer {

    @Autowired@SuppressWarnings("all")
    private DebugContextManager applicationContextManager;

    @Autowired
    private RedisSupport<String> redis;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DebugHandlerInterceptor());
    }

    class DebugHandlerInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String debugId = request.getHeader("NK-Debug");
            if(StringUtils.isNotBlank(debugId)){
                response.setHeader("NK-Debug-Log", UUID.randomUUID().toString());
                applicationContextManager.startThreadLocal(debugId);
            }
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            String output = applicationContextManager.exitThreadLocal();
            String logId = response.getHeader("NK-Debug-Log");
            if(output!=null&&logId!=null){
                redis.set(logId,output);
                redis.expire(logId,30);
            }
        }
    }
}
