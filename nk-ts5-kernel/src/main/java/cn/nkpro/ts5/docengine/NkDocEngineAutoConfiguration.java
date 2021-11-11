package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.docengine.service.NkBpmTaskService;
import cn.nkpro.ts5.docengine.service.NkDocEngineContext;
import cn.nkpro.ts5.docengine.service.SequenceSupport;
import cn.nkpro.ts5.docengine.service.impl.DefaultBpmTaskServiceImpl;
import cn.nkpro.ts5.docengine.service.impl.DefaultSequenceSupportImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class NkDocEngineAutoConfiguration implements ApplicationRunner, WebMvcConfigurer {

    private final NkDocSearchService searchService;

    public NkDocEngineAutoConfiguration(@Autowired NkDocSearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        searchService.init();
    }
    /**
     * 单据编号序列配置
     */
    @ConditionalOnMissingBean
    @Bean
    public SequenceSupport sequenceSupport(){
        return new DefaultSequenceSupportImpl();
    }


    @ConditionalOnMissingBean
    @Bean
    public NkBpmTaskService nkBpmTaskService(){
        return new DefaultBpmTaskServiceImpl();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ThreadLocalClearInterceptor());
    }

    public static class ThreadLocalClearInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            NkDocEngineContext.clear();
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
            NkDocEngineContext.clear();
        }
    }
}
