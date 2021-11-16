package cn.nkpro.ts5.co;

import cn.nkpro.ts5.co.fs.FSSupport;
import cn.nkpro.ts5.co.fs.defaults.DefaultFileSupportAliyunOSS;
import cn.nkpro.ts5.co.fs.defaults.DefaultFileSupportHuaweiOBS;
import cn.nkpro.ts5.co.fs.defaults.DefaultFileSupportImpl;
import cn.nkpro.ts5.co.fs.properties.AliyunProperties;
import cn.nkpro.ts5.co.fs.properties.HuaweiProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@EnableConfigurationProperties({
        AliyunProperties.class,
        HuaweiProperties.class,
})
public class FSAutoConfiguration {

    /**
     * 文件上传配置 华为OBS
     */
    @Order(0)
    @ConditionalOnMissingBean
    @ConditionalOnProperty("tfms.huawei.obs.bucket")
    @Bean
    public FSSupport defaultFileSupportHuaweiOBS(){
        return new DefaultFileSupportHuaweiOBS();
    }

    /**
     * 文件上传配置 阿里云OSS
     */
    @Order(1)
    @ConditionalOnMissingBean
    @ConditionalOnProperty("tfms.aliyun.oss.bucket")
    @Bean
    public FSSupport defaultAliyunOSSFileSupport(){
        return new DefaultFileSupportAliyunOSS();
    }

    /***
     * 文件上传配置 本地服务
     */
    @Order
    @ConditionalOnMissingBean
    @Bean
    public FSSupport defaultFileSupport(){
        return new DefaultFileSupportImpl();
    }
}
