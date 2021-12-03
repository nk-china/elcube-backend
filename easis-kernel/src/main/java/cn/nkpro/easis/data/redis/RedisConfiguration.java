/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.data.redis;

import cn.nkpro.easis.basic.NkProperties;
import cn.nkpro.easis.data.redis.defaults.DefaultRedisSupportImpl;
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
     * RedisTemplate相关配置
     */
    @Bean
    public EnvRedisTemplate<?> redisTemplate(RedisConnectionFactory factory,NkProperties nkProperties) {
        return (EnvRedisTemplate<?>) new EnvRedisTemplate(factory,nkProperties.getEnvKey());
    }

    /**
     * redis 配置 默认使用Spring RedisTemplate的Support实现
     */
    @ConditionalOnMissingBean
    @Bean
    public RedisSupport redisSupport(){
        return new DefaultRedisSupportImpl();
    }

}
