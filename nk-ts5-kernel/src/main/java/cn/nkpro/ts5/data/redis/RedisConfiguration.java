package cn.nkpro.ts5.data.redis;

import cn.nkpro.ts5.basic.NkProperties;
import cn.nkpro.ts5.data.redis.defaults.DefaultRedisSupportImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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

    /**
     * retemplate相关配置
     * @param factory
     * @return
     */
    @ConditionalOnClass(NkProperties.class)
    @ConditionalOnBean(NkProperties.class)
    @Bean
    public EnvRedisTemplate<?> redisTemplate(RedisConnectionFactory factory,NkProperties nkProperties) {
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
