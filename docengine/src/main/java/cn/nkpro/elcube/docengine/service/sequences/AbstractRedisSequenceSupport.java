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
package cn.nkpro.elcube.docengine.service.sequences;

import cn.nkpro.elcube.basic.Constants;
import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.docengine.EnumDocClassify;
import cn.nkpro.elcube.docengine.service.SequenceSupport;
import cn.nkpro.elcube.exception.NkSystemException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by bean on 2020/7/7.
 */
@SuppressWarnings("all")
public abstract class AbstractRedisSequenceSupport implements SequenceSupport {

    @Autowired
    private RedisSupport<Long> redisSupport;

    protected Long increment(EnumDocClassify classify, String docType, String redisKey){

        String key = String.format("%s:%s", Constants.CACHE_SEQUENCE, redisKey);
        if(redisSupport.exists(key)){
            return redisSupport.increment(key, 1L);
        }

        String lockValue = UUID.randomUUID().toString();

        AtomicReference<Long> atomic = new AtomicReference();

        redisSupport.lockRun(getClass().getName(), lockValue,()->{

            long prev = prev(classify, docType);
            // 返回 +1
            redisSupport.set(key, prev + 1);

            atomic.set(prev + 1);

            return null;

        },()->{

        },()->{
            for(int i=0;i<10;i++){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new NkSystemException("获取序列号发生错误");
                }
                if(redisSupport.exists(key)){
                    atomic.set(redisSupport.increment(key, 1L));
                    //return redisSupport.increment(key, 1L);
                }
            }
            throw new NkSystemException("获取序列号发生错误");
        });

        return atomic.get();

//        boolean lock = redisSupport.lock(getClass().getName(), lockValue, 600);
//
//        if(lock){
//            try{
//                long prev = prev(classify, docType);
//                // 返回 +1
//                redisSupport.set(key, prev + 1);
//                return prev + 1;
//            }finally {
//                redisSupport.unLock(getClass().getName(),lockValue);
//            }
//        }else{
//            for(int i=0;i<10;i++){
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    throw new NkSystemException("获取序列号发生错误");
//                }
//                if(redisSupport.exists(key)){
//                    return redisSupport.increment(key, 1L);
//                }
//            }
//            throw new NkSystemException("获取序列号发生错误");
//        }
    }

    abstract long prev(EnumDocClassify classify, String docType);
}
