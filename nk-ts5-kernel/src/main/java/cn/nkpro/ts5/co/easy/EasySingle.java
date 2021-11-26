package cn.nkpro.ts5.co.easy;

import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

@SuppressWarnings("all")
public interface EasySingle {

    Object target();

    EasySingle set(String key, Object value);

    <T> T get(String key);

    int keySize();

    public static EasySingle from(Object target){
        Assert.notNull(target,"目标数据不能为null");
        Assert.isTrue(!(target instanceof Collection || target.getClass().isArray()),"目标数据必须是 Entity 或 Map 子集");

        if(target instanceof Map){
            return new EasyMap((Map<Object, Object>) target);
        }
        return new EasyEntity(target);
    }
}
