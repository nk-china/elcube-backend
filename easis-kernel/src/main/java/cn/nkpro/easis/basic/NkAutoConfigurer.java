package cn.nkpro.easis.basic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings("all")
@Configuration
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@MapperScan(basePackages = {
        "cn.nkpro.easis.platform.gen",
        "cn.nkpro.easis.security.gen",
        "cn.nkpro.easis.docengine.gen",
})
@AutoConfigureOrder
@EnableConfigurationProperties(NkProperties.class)
public class NkAutoConfigurer {
    /**
     * ID序列配置 默认UUID
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public GUID guid(){
        return new GUID(){};
    }

    @ConditionalOnMissingBean
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    @ConditionalOnMissingBean
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(30*1000);
        factory.setConnectTimeout(10*1000);
        return factory;
    }

    @ConditionalOnMissingBean(name="nkTaskExecutor")
    @Bean("nkTaskExecutor")
    public ThreadPoolTaskExecutor nkTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(32);
        taskExecutor.setMaxPoolSize(2147483647);
        taskExecutor.setQueueCapacity(2147483647);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("nk-async-executor-");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        return taskExecutor;
    }
}
