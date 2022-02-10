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
package cn.nkpro.elcube.basic;

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
        "cn.nkpro.elcube.platform.gen",
        "cn.nkpro.elcube.security.gen",
        "cn.nkpro.elcube.docengine.gen",
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

//    @ConditionalOnMissingBean
//    @Bean
//    public NkMobileService nkMobileService(){
//        return new NkMobileServiceImpl();
//    }
//
//    @ConditionalOnMissingBean
//    @Bean
//    public NkAccountOperationService nkAccountOperationService(){
//        return new NkAccountOperationServiceImpl();
//    }
}
