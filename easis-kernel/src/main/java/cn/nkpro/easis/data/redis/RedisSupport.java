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

import cn.nkpro.easis.exception.abstracts.NkRuntimeException;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;
import java.util.Map;

/**
 * 时间单位 秒
 * Created by bean on 2020/7/24.
 */
@SuppressWarnings("unused")
public interface RedisSupport<T> {

    void    clear();
    boolean exists(String key);
    void    expire(String key, long expire);

    Long getExpire(String key);

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

    boolean lock(String key, String value, int retry, int interval, int expire);
    boolean lock(String key, String value, int expire);
    void    unLock(String key, String value);

    interface Function<T>{
        T apply() throws NkRuntimeException;
    }
}
