package cn.nkpro.ts5.docengine.service.sequences;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.data.redis.RedisSupport;
import cn.nkpro.ts5.docengine.EnumDocClassify;
import cn.nkpro.ts5.docengine.gen.DocH;
import cn.nkpro.ts5.docengine.gen.DocHExample;
import cn.nkpro.ts5.docengine.gen.DocHMapper;
import cn.nkpro.ts5.docengine.service.SequenceSupport;
import cn.nkpro.ts5.exception.NkSystemException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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
