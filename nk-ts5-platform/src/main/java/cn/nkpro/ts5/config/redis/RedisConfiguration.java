package cn.nkpro.ts5.config.redis;

import cn.nkpro.ts5.config.NkProperties;
import cn.nkpro.ts5.config.redis.defaults.DefaultRedisSupportImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableConfigurationProperties({
        NkProperties.class,
})
public class RedisConfiguration {

    @Autowired
    private NkProperties nkProperties;

    /**
     * retemplate相关配置
     * @param factory
     * @return
     */
    @Bean
    public EnvRedisTemplate<?> redisTemplate(RedisConnectionFactory factory) {
        return (EnvRedisTemplate<?>) new EnvRedisTemplate(factory,nkProperties.getEnvKey());
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

}
