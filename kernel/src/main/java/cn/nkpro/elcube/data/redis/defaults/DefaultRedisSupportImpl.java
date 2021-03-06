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
package cn.nkpro.elcube.data.redis.defaults;

import cn.nkpro.elcube.data.redis.EnvRedisTemplate;
import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.exception.abstracts.NkRuntimeException;
import cn.nkpro.elcube.utils.UUIDHexGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * Redis Template 的代理程序
 *
 * 对RedisTemplate进行简单包装，方便使用
 *
 * Created by bean on 2020/7/24.
 */
@Slf4j
public class DefaultRedisSupportImpl<T> implements RedisSupport<T> {

    @Autowired@SuppressWarnings("all")
    private EnvRedisTemplate<T> redisTemplate;
    @Autowired@SuppressWarnings("all")
    private EnvRedisTemplate<String> stringRedisTemplate;

    @Override
    public Set<String> keys(String pattern){
        return redisTemplate.keys(pattern);
    }

    @Override
    public void clear() {

        List<String> keysKeepPrefix = Arrays.asList("stream","debug","lock","keep","runtime");

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
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    @Override
    public long increment(String key, long l) {
        Long L = redisTemplate.opsForValue().increment(key,l);
        Assert.notNull(L,"Redis出现错误");
        return L;
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
    public String lock(String id, int expireSeconds){
        String value = UUIDHexGenerator.generate();
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        Boolean setIfAbsent = ops.setIfAbsent("LOCK:" + id, value);
        if(setIfAbsent!=null && setIfAbsent){
            redisTemplate.expire("LOCK:" + id, expireSeconds, TimeUnit.SECONDS);
            return value;
        }
        return null;
    }

    @Override
    public void unlock(String id, String value){
        String s = stringRedisTemplate.opsForValue().get("LOCK:" + id);
        if(Objects.equals(s,value)){
            stringRedisTemplate.delete("LOCK:" + id);
        }
    }
//    /**
//     *
//     * 带重试机制的锁定
//     * 锁定后立即运行回调函数，回调函数执行后，不立即解锁
//     *
//     * 如果当前上下文有事务，将在事务提交或回滚后解锁，解锁后回调 afterUnLock
//     *
//     * 锁的最长有效期1小时
//     *
//     * @param key 锁的KEY
//     * @param value 锁的值，非空即可
//     * @param retry 重试次数
//     * @param interval 重试间隔（单位毫秒）
//     * @param runLocked 锁定后回调函数
//     * @param afterUnLock 锁定解除后回调函数
//     * @param runLockFailed 锁定失败后回调函数
//     * @param <R> 锁定后回调函数返回数据类型
//     * @return 回调函数返回值
//     */
//    @Override
//    public <R> R  lockRunInTransaction(
//            @NotNull String key,
//            @NotNull String value,
//            int retry,
//            int interval,
//            @NotNull Function<R> runLocked,
//            FunctionRun afterUnLock,
//            FunctionRun runLockFailed) {
//        return execLockRun(key,value,retry,interval,runLocked,afterUnLock,runLockFailed,true);
//    }
//
//    /**
//     * 带重试机制的锁定
//     * 锁定后立即运行回调函数，回调函数执行后，立即解锁
//     *
//     * 锁的最长有效期1小时
//     *
//     * @param key 锁的KEY
//     * @param value 锁的值，非空即可
//     * @param retry 重试次数
//     * @param interval 重试间隔（单位毫秒）
//     * @param runLocked 锁定后回调函数
//     * @param afterUnLock 锁定解除后回调函数
//     * @param runLockFailed 锁定失败后回调函数
//     * @param <R> 锁定后回调函数返回数据类型
//     * @return 回调函数返回值
//     */
//    @Override
//    public <R> R  lockRun(
//            @NotNull String key,
//            @NotNull String value,
//            int retry,
//            int interval,
//            @NotNull Function<R> runLocked,
//            FunctionRun afterUnLock,
//            FunctionRun runLockFailed) {
//        return execLockRun(key,value,retry,interval,runLocked,afterUnLock,runLockFailed,false);
//    }
//
//    private <R> R  execLockRun(
//            @NotNull String key,
//            @NotNull String value,
//            int retry,
//            int interval,
//            @NotNull Function<R> runLocked,
//            FunctionRun afterUnLock,
//            FunctionRun runLockFailed,
//            boolean transaction) {
//        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
//        int i=0;
//        boolean locked = false;
//        do{
//            Boolean ret = ops.setIfAbsent("LOCK:"+key, value);
//            if(ret != null && ret){
//                redisTemplate.expire(key,60 * 60, TimeUnit.SECONDS);
//                locked = true;
//                break;
//            }
//            log.debug("尝试获取锁[{}]失败，{}ms后重试", key, interval);
//            try {
//                Thread.sleep(interval);
//            } catch (InterruptedException e) {
//                throw new NkSystemException(e.getMessage(),e);
//            }
//        }while (++i <= retry);
//
//        if(locked){
//            try{
//                return runLocked.apply();
//            }finally {
//                if(transaction){
//                    TransactionSync.runAfterCompletionLast("解除Redis锁",(status)-> {
//                        stringRedisTemplate.delete("LOCK:" + key);
//                        if(afterUnLock!=null){
//                            afterUnLock.apply();
//                        }
//                    });
//                }else{
//                    stringRedisTemplate.delete("LOCK:" + key);
//                    if(afterUnLock!=null){
//                        afterUnLock.apply();
//                    }
//                }
//            }
//        }else{
//            log.info("尝试获取锁[{}]失败，请检查是否有死锁", key);
//            if(runLockFailed!=null){
//                runLockFailed.apply();
//            }
//            return null;
//        }
//    }
//
//    /**
//     * 锁定后立即运行回调函数，回调函数执行后，立即解锁
//     *
//     * 锁的最长有效期1小时
//     * @param key 锁的KEY
//     * @param value 锁的值，非空即可
//     * @param runLocked 锁定后回调函数
//     * @param afterUnLock 锁定解除后回调函数
//     * @param runLockFailed 锁定失败后回调函数
//     * @param <R> 锁定后回调函数返回数据类型
//     * @return 回调函数返回值
//     */
//    @Override
//    public <R> R lockRun(@NotNull String key,
//                         @NotNull String value,
//                         @NotNull Function<R> runLocked,
//                         FunctionRun afterUnLock,
//                         FunctionRun runLockFailed) {
//        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
//        Boolean ret = ops.setIfAbsent("LOCK:"+key, value);
//
//        if(ret != null && ret){
//            redisTemplate.expire(key,60 * 60, TimeUnit.SECONDS);
//            try{
//                return runLocked.apply();
//            }finally {
//                stringRedisTemplate.delete("LOCK:" + key);
//                if(afterUnLock!=null){
//                    afterUnLock.apply();
//                }
//            }
//        }else{
//            if(runLockFailed!=null){
//                runLockFailed.apply();
//            }
//            return null;
//        }
//    }
//
//    禁止自行解锁，必须在lockRun的回调函数中，对锁定的对象进行数据操作
//    @Override
//    public void unLock(String key, String value){
//        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
//        String s = ops.get("LOCK:" + key);
//        if(s!=null && StringUtils.equals(s,value)){
//            stringRedisTemplate.delete("LOCK:" + key);
//        }
//    }
}
