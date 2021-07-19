package cn.nkpro.ts5.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

public class NKRedisTemplate<V> extends RedisTemplate<String, V> implements InitializingBean {

    private String prefix;

    public NKRedisTemplate(RedisConnectionFactory factory, String prefix) {

        this.prefix = prefix;

        setKeySerializer(new TfmsStringRedisSerializer());
        setValueSerializer(new GenericJackson2JsonRedisSerializer());
        setHashKeySerializer(new StringRedisSerializer());
        setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        setConnectionFactory(factory);

        afterPropertiesSet();
    }

    public String buildKey(String key){
        if(StringUtils.isNotBlank(prefix)){
            return String.format("%s:%s",prefix, key);
        }
        return key;
    }

    class TfmsStringRedisSerializer extends StringRedisSerializer {

        @Override
        public String deserialize(@Nullable byte[] bytes) {
            String key = super.deserialize(bytes);
            if(StringUtils.isNotBlank(prefix) && key!=null){
                return key.substring(prefix.length()+1);
            }
            return key;
        }

        @Override
        public byte[] serialize(@Nullable String string) {
            return super.serialize(buildKey(string));
        }
    }
}
