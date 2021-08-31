package cn.nkpro.ts5.config.sheduled;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadConfig {

    @ConditionalOnMissingBean(name="nkTaskExecutor")
    @Bean
    public ThreadPoolTaskExecutor nkTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setQueueCapacity(2147483647);
        taskExecutor.setThreadNamePrefix("nk-async-executor-");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        return taskExecutor;
    }
}
