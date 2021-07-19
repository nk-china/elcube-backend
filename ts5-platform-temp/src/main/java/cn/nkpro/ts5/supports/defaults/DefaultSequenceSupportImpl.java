package cn.nkpro.ts5.supports.defaults;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.supports.SequenceSupport;
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
    public String next(EnumDocClassify classify,String docType){
        if(classify==EnumDocClassify.PROJECT){
            return String.format("PRJ%09d",next("PRJ"));
        }
        return String.format("DOC%09d",next("DOC"));
    }
}
