package cn.nkpro.tfms.platform.basis.defaults;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.mappers.gen.DefConstantMapper;
import cn.nkpro.tfms.platform.model.po.DefConstant;
import cn.nkpro.tfms.platform.basis.TfmsSpELInjection;
import cn.nkpro.ts5.supports.RedisSupport;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NkDefaultSpELConstantInjection implements TfmsSpELInjection {

    @Getter
    private String spELName = "constant";

    @Autowired
    private RedisSupport<String> redisSupport;
    @Autowired
    private DefConstantMapper constantMapper;

    public String text(String key){
        return getValue(key);
    }

    private String getValue(String key){
        return redisSupport.getIfAbsent(Constants.CACHE_DEF_CONSTANT,key,()->{
            DefConstant defConstant = constantMapper.selectByPrimaryKey(key);
            if(defConstant!=null){
                return defConstant.getConstantValue();
            }
            return null;
        });
    }
}
