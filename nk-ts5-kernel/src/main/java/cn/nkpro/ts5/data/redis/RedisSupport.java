package cn.nkpro.ts5.data.redis;

import cn.nkpro.ts5.exception.abstracts.NkRuntimeException;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;
import java.util.Map;

/**
 * 时间单位 秒
 * Created by bean on 2020/7/24.
 */
@SuppressWarnings("unused")
public interface RedisSupport<T> {

    @Scheduled(cron = "0 * * * * ?")
    void heartbeat();

    void    clear();
    boolean exists(String key);
    void    expire(String key, long expire);
    long    increment(String key, long l);

    void set(String key, T value);
    T    get(String key);
    T    getIfAbsent(String key, Function<T> mapper) throws NkRuntimeException;
    T    getIfAbsent(String key, boolean cacheNullValue, Function<T> mapper) throws NkRuntimeException;
    void delete(String... key);
    void delete(Collection<String> key);
    void deletes(String keysLike);

    void            set(String hash, String key, T value);
    T               get(String hash, String key);
    T               getIfAbsent(String hash, String hashKey, Function<T> mapper) throws NkRuntimeException;
    T               getIfAbsent(String hash, String hashKey, boolean cacheNullValue,Function<T> mapper) throws NkRuntimeException;
    Map<String, T>  getHash(String hash);
    Map<String, T>  getHash(String hash, String... keys);
    Map<String, T>  getHash(String hash, Collection<String> keys);
    Map<String, T>  getHashIfAbsent(String hash, Function<Map<String,T>> mapper);
    void            deleteHash(String hash, Object... keys);
    void            deleteHash(String hash, Collection<String> keys);

    boolean lock(String key, String value, int retry, int expire);
    boolean lock(String key, String value, int expire);
    void    unLock(String key, String value);

    interface Function<T>{
        T apply() throws NkRuntimeException;
    }
}
