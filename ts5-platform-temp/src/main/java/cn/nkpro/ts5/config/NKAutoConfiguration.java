package cn.nkpro.ts5.config;

import cn.nkpro.ts5.supports.FileSupport;
import cn.nkpro.ts5.supports.GUID;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.supports.SequenceSupport;
import cn.nkpro.ts5.supports.defaults.DefaultRedisSupportImpl;
import cn.nkpro.ts5.supports.defaults.DefaultSequenceSupportImpl;
import cn.nkpro.ts5.supports.defaults.file.DefaultFileSupportAliyunOSS;
import cn.nkpro.ts5.supports.defaults.file.DefaultFileSupportHuaweiOBS;
import cn.nkpro.ts5.supports.defaults.file.DefaultFileSupportImpl;
import cn.nkpro.ts5.config.nk.AliyunProperties;
import cn.nkpro.ts5.config.nk.HuaweiProperties;
import cn.nkpro.ts5.config.nk.NKProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableConfigurationProperties({
        NKProperties.class,
        AliyunProperties.class,
        HuaweiProperties.class,
})
public class NKAutoConfiguration {

    @Autowired
    private NKProperties nkProperties;

    /**
     * ID序列配置 默认UUID
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public GUID guid(){
        return new GUID(){};
    }


    /**
     * retemplate相关配置
     * @param factory
     * @return
     */
    @Bean
    public NKRedisTemplate<?> redisTemplate(RedisConnectionFactory factory) {
        return (NKRedisTemplate<?>) new NKRedisTemplate(factory,nkProperties.getEnvKey());
    }

    /**
     * 单据编号序列配置
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public SequenceSupport sequenceSupport(){
        return new DefaultSequenceSupportImpl();
    }

    /**
     * redis 配置 默认使用Spring RedisTemplate的Support实现
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public RedisSupport redisSupport(){
        return new DefaultRedisSupportImpl();
    }

    /**
     * 文件上传配置 华为OBS
     * @return
     */
    @Order(0)
    @ConditionalOnMissingBean
    @ConditionalOnProperty("tfms.huawei.obs.bucket")
    @Bean
    public FileSupport defaultFileSupportHuaweiOBS(){
        return new DefaultFileSupportHuaweiOBS();
    }

    /**
     * 文件上传配置 阿里云OSS
     * @return
     */
    @Order(1)
    @ConditionalOnMissingBean
    @ConditionalOnProperty("tfms.aliyun.oss.bucket")
    @Bean
    public FileSupport defaultAliyunOSSFileSupport(){
        return new DefaultFileSupportAliyunOSS();
    }

    /***
     * 文件上传配置 本地服务
     * @return
     */
    @Order
    @ConditionalOnMissingBean
    @Bean
    public FileSupport defaultFileSupport(){
        return new DefaultFileSupportImpl();
    }
}
