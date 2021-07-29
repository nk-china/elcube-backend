package cn.nkpro.ts5.config.id.defaults;

import cn.nkpro.ts5.basic.Constants;
import cn.nkpro.ts5.engine.doc.NKDocProcessor;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.config.id.SequenceSupport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bean on 2020/7/7.
 */
public class DefaultSequenceSupportImpl implements SequenceSupport {

    @Autowired
    private RedisSupport<Long> redisSupport;

    private Long next(String key){
        return redisSupport.increment(String.format("%s:%s", Constants.CACHE_SEQUENCE,key),1L);
    }

    @Override
    public String next(NKDocProcessor.EnumDocClassify classify, String docType){
        return String.format("DOC%09d",next("DOC"));
    }
}
