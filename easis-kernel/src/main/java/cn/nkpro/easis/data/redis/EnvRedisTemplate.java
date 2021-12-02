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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

public class EnvRedisTemplate<V> extends RedisTemplate<String, V> implements InitializingBean {

    private String env;

    public EnvRedisTemplate(RedisConnectionFactory factory, String env) {

        this.env = env;

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.EXISTING_PROPERTY);

        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        setKeySerializer(new EnvStringRedisSerializer());
        setValueSerializer(jsonRedisSerializer);
        setHashKeySerializer(new StringRedisSerializer());
        setHashValueSerializer(jsonRedisSerializer);
        setConnectionFactory(factory);

        afterPropertiesSet();
    }

    private String buildKey(String key){
        if(StringUtils.isNotBlank(env)){
            return String.format("%s:%s", env, key);
        }
        return key;
    }

    class EnvStringRedisSerializer extends StringRedisSerializer {

        @Override
        public String deserialize(@Nullable byte[] bytes) {
            String key = super.deserialize(bytes);
            if(StringUtils.isNotBlank(env) && key!=null){
                return key.substring(env.length()+1);
            }
            return key;
        }

        @Override
        public byte[] serialize(@Nullable String string) {
            return super.serialize(buildKey(string));
        }
    }
}
