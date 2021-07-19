package cn.nkpro.ts5.supports.defaults;

import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.config.NKRedisTemplate;
import cn.nkpro.ts5.supports.RedisSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/7/24.
 */
@Slf4j
public class DefaultRedisSupportImpl<T> implements RedisSupport<T>,ApplicationContextAware{

    @Autowired
    private NKRedisTemplate<T> redisTemplate;

    @Scheduled(cron = "0 * * * * ?")
    public void heartbeat(){
        log.debug("redis heartbeat : " + redisTemplate.opsForValue().get("__.heartbeat"));
    }


    public T getIfAbsent(String hash,String hashKey,Function<T> mapper) throws TfmsException {
        return getIfAbsent(hash, hashKey, false, mapper);
    }
    public T getIfAbsent(String hash,String hashKey,boolean cacheNullValue,Function<T> mapper) throws TfmsException {
        Assert.notNull(hash,"hash不能为空");
        Assert.notNull(hashKey,"hashKey不能为空");

        HashOperations<String,String,T> hashOperations = redisTemplate.opsForHash();
        T value = hashOperations.get(hash, hashKey);
        if(value == null){
            value = mapper.apply();
            if(cacheNullValue || value != null){
                hashOperations.put(hash,hashKey,value);
            }
        }
        return value;
    }

    @Override
    public Map<String,T> getHash(String hash, Collection<String> keys){
        Assert.notNull(hash,"hash不能为空");

        HashOperations<String,String,T> hashOperations = redisTemplate.opsForHash();
        List<T> list = hashOperations.multiGet(hash, keys);

        Map<String,T> data = new HashMap<>();
        List<String> listKey = new ArrayList<>(keys);
        listKey.forEach(key-> data.put(key,list.get(listKey.indexOf(key))));

        return data;
    }

    @Override
    public void putHash(String hash, String key, T value) {
        redisTemplate.opsForHash().put(hash,key,value);
    }

    public T getIfAbsent(String key, Function<T> mapper) throws TfmsException {
        return getIfAbsent(key, false, mapper);
    }

    public T getIfAbsent(String key,boolean cacheNullValue,Function<T> mapper) throws TfmsException {
        Assert.notNull(key,"key不能为空");

        ValueOperations<String, T> valueOperations = redisTemplate.opsForValue();
        T value = valueOperations.get(key);
        if(value == null){
            value = mapper.apply();
            if(cacheNullValue || value != null){
                valueOperations.set(key,value);
            }
        }
        return value;
    }

    public void deletes(String keysLike) {
        Set<String> keys = redisTemplate.keys(keysLike);
        if(keys!=null)
            redisTemplate.delete(keys);
    }

    public void delete(String key){
        Assert.notNull(key,"key不能为空");
        redisTemplate.delete(key);
    }

    public void delete(String hash, Object... hashKey){
        Assert.notNull(hash,"hash不能为空");
        Assert.notNull(hashKey,"hashKey不能为空");
        Assert.isTrue(hashKey.length>0,"hashKey不能为空");
        redisTemplate.opsForHash().delete(hash,hashKey);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    }

    public Long increment(String key, long l) {
        return redisTemplate.opsForValue().increment(key,l);
    }

    public void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public T get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void expire(String key, long timeout) {
        redisTemplate.expire(key,timeout, TimeUnit.SECONDS);
    }

    @Override
    public void clear() {
        Optional.ofNullable(redisTemplate.keys("*"))
                .ifPresent(list->{
                    List<String> collect = list.stream()
                            .filter(key -> !(key.startsWith("stream.") || key.startsWith("stream:")))
                            .collect(Collectors.toList());
                    if(collect.size()>0)
                        redisTemplate.delete(collect);
                });
    }


    @Override
    public Boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }


}
