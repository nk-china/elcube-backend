package cn.nkpro.ts5.config;

import cn.nkpro.ts5.config.mvc.CompressReturnHandler;
import cn.nkpro.ts5.config.nk.NKProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created by bean on 2019/12/30.
 */
@Configuration
public class NKWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private NKProperties properties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ThreadLocalClearInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        returnValueHandlers.add(new CompressReturnHandler(properties.getResponseCompress()));
        //returnValueHandlers.add(new PagedListReturnValueHandler());
    }
}
