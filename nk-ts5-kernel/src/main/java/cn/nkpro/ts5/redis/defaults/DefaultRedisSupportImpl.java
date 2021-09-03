package cn.nkpro.ts5.redis.defaults;

import cn.nkpro.ts5.exception.TfmsSystemException;
import cn.nkpro.ts5.exception.abstracts.NkRuntimeException;
import cn.nkpro.ts5.redis.EnvRedisTemplate;
import cn.nkpro.ts5.redis.RedisSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DefaultRedisSupportImpl<T> implements RedisSupport<T> {

    @Autowired@SuppressWarnings("all")
    private EnvRedisTemplate<T> redisTemplate;
    @Autowired@SuppressWarnings("all")
    private EnvRedisTemplate<String> stringRedisTemplate;

    @Scheduled(cron = "0 * * * * ?")
    public void heartbeat(){
        redisTemplate.opsForValue().get("__.heartbeat");
    }

    @Override
    public void clear() {

        List<String> keysKeepPrefix = Arrays.asList("stream","debug","lock","keep");

        Optional.ofNullable(redisTemplate.keys("*"))
            .ifPresent(list->{
                List<String> collect = list.stream()
                        .filter(key ->
                            keysKeepPrefix.stream()
                                .noneMatch(pre->{
                                    String keyUpper = key.toUpperCase();
                                    String preUpper = pre.toUpperCase();
                                    return (keyUpper.startsWith(preUpper+":")||keyUpper.startsWith(preUpper+"_"));
                                })
                        )
                        .collect(Collectors.toList());
                if(collect.size()>0)
                    redisTemplate.delete(collect);
            });
    }
    @Override
    public boolean exists(String key){
        Boolean B = redisTemplate.hasKey(key);
        return B == null ? false : B;
    }
    @Override
    public void expire(String key, long timeout) {
        redisTemplate.expire(key,timeout, TimeUnit.SECONDS);
    }
    @Override
    public long increment(String key, long l) {
        Long L = redisTemplate.opsForValue().increment(key,l);
        return L == null ? 0 : L;
    }



    @Override
    public void set(String key, T value) {
        redisTemplate.opsForValue().set(key, value);
    }
    @Override
    public T get(String key){
        return redisTemplate.opsForValue().get(key);
    }
    @Override
    public T getIfAbsent(String key, Function<T> mapper) throws NkRuntimeException {
        return getIfAbsent(key, false, mapper);
    }
    @Override
    public T getIfAbsent(String key,boolean cacheNullValue,Function<T> mapper) throws NkRuntimeException {
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
    @Override
    public void delete(String... key){
        Assert.notNull(key,"key不能为空");
        redisTemplate.delete(Arrays.asList(key));
    }
    @Override
    public void delete(Collection<String> key){
        Assert.notNull(key,"key不能为空");
        redisTemplate.delete(key);
    }
    @Override
    public void deletes(String keysLike) {
        Assert.notNull(keysLike,"key不能为空");
        Set<String> keys = redisTemplate.keys(keysLike);
        if(keys!=null)
            redisTemplate.delete(keys);
    }




    @Override
    public void set(String hash, String key, T value) {
        redisTemplate.opsForHash().put(hash,key,value);
    }
    @Override
    public T get(String hash, String key) {
        HashOperations<String,String,T> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(hash,key);
    }
    @Override
    public T getIfAbsent(String hash,String keys,Function<T> mapper) throws NkRuntimeException {
        return getIfAbsent(hash, keys, false, mapper);
    }
    @Override
    public T getIfAbsent(String hash,String keys,boolean cacheNullValue,Function<T> mapper) throws NkRuntimeException {
        Assert.notNull(hash,"hash不能为空");
        Assert.notNull(keys,"keys不能为空");

        HashOperations<String,String,T> hashOperations = redisTemplate.opsForHash();
        T value = hashOperations.get(hash, keys);
        if(value == null){
            value = mapper.apply();
            if(cacheNullValue || value != null){
                hashOperations.put(hash,keys,value);
            }
        }
        return value;
    }
    @Override
    public Map<String,T> getHash(String hash){
        Assert.notNull(hash,"hash不能为空");

        HashOperations<String,String,T> hashOperations = redisTemplate.opsForHash();
        return hashOperations.entries(hash);
    }
    @Override
    public Map<String, T> getHash(String hash, String... keys) {
        return getHash(hash, Arrays.asList(keys));
    }
    @Override
    public Map<String,T> getHash(String hash, Collection<String> keys){
        Assert.notNull(hash,"hash不能为空");
        Assert.notNull(keys,"keys不能为空");
        Assert.notEmpty(keys,"keys不能为空");

        HashOperations<String,String,T> hashOperations = redisTemplate.opsForHash();

        List<T> list = hashOperations.multiGet(hash, keys);
        List<String> listKey = new ArrayList<>(keys);

        return keys.stream()
            .collect(Collectors.toMap(
                    key->key,
                    key-> list.get(listKey.indexOf(key))
            ));
    }
    @Override
    public Map<String, T> getHashIfAbsent(String hash, Function<Map<String, T>> mapper) {
        Assert.notNull(hash,"hash不能为空");

        HashOperations<String,String,T> hashOperations = redisTemplate.opsForHash();
        Map<String,T> value =  hashOperations.entries(hash);
        if(MapUtils.isEmpty(value)){
            value = mapper.apply();
            if(MapUtils.isNotEmpty(value)){
                hashOperations.putAll(hash,value);
            }
        }
        return value;
    }
    @Override
    public void deleteHash(String hash, Object... keys){
        Assert.notNull(hash,"hash不能为空");
        Assert.notNull(keys,"keys不能为空");
        Assert.notEmpty(keys,"keys不能为空");
        redisTemplate.opsForHash().delete(hash,keys);
    }
    @Override
    public void deleteHash(String hash, Collection<String> keys) {
        deleteHash(hash, keys.toArray());
    }

    @Override
    public boolean lock(String key, String value, int retry, int expire) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        int i=0;
        do{
            Boolean ret = ops.setIfAbsent("LOCK:"+key, value, expire, TimeUnit.SECONDS);
            if(ret != null && ret){
                return true;
            }
            log.debug("尝试获取锁[{}]失败，1s后重试", key);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new TfmsSystemException(e.getMessage(),e);
            }
        }while (++i <= retry);
        log.info("尝试获取锁[{}]失败，请检查是否有死锁", key);

        return false;
    }
    @Override
    public boolean lock(String key, String value, int expire) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        Boolean ret = ops.setIfAbsent("LOCK:"+key, value, expire, TimeUnit.SECONDS);
        return ret != null && ret;
    }
    @Override
    public void unLock(String key, String value){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String s = ops.get("LOCK:" + key);
        if(s!=null && StringUtils.equals(s,value)){
            stringRedisTemplate.delete("LOCK:" + key);
        }
    }
}
