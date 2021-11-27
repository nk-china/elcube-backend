package cn.nkpro.easis.docengine.service.sequences;

import cn.nkpro.easis.basic.Constants;
import cn.nkpro.easis.data.redis.RedisSupport;
import cn.nkpro.easis.docengine.EnumDocClassify;
import cn.nkpro.easis.docengine.service.SequenceSupport;
import cn.nkpro.easis.exception.NkSystemException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

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
        boolean lock = redisSupport.lock(getClass().getName(), lockValue, 600);

        if(lock){
            try{
                long prev = prev(classify, docType);
                // 返回 +1
                redisSupport.set(key, prev + 1);
                return prev + 1;
            }finally {
                redisSupport.unLock(getClass().getName(),lockValue);
            }
        }else{
            for(int i=0;i<10;i++){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new NkSystemException("获取序列号发生错误");
                }
                if(redisSupport.exists(key)){
                    return redisSupport.increment(key, 1L);
                }
            }
            throw new NkSystemException("获取序列号发生错误");
        }
    }

    abstract long prev(EnumDocClassify classify, String docType);
}
