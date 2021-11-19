package cn.nkpro.ts5.docengine.model.easy;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;

public class EasyEntity implements EasySingle {

    private Object target;

    @Override
    public Object target() {
        return target;
    }

    EasyEntity(Object target) {
        Assert.notNull(target,"目标数据不能为null");
        Assert.isTrue(!(target instanceof Collection || target.getClass().isArray()),"目标数据必须是 Entity 或 Map 子集");
        this.target = target;
    }

    @Override
    public EasyEntity set(String key, Object value){

        Field field = ReflectionUtils.findField(target.getClass(), key);

        if(field==null)
            return this;

        Assert.notNull(field,String.format("没有找到%s对象的%s字段",target, key));
        field.setAccessible(true);
        ReflectionUtils.setField(field,target,value);

        return this;
    }

    @SuppressWarnings("all")
    @Override
    public <T> T get(String key){
        Field field = ReflectionUtils.findField(target.getClass(), key);

        if(field==null)
            return null;

        Assert.notNull(field,String.format("没有找到%s对象的%s字段",target, key));
        field.setAccessible(true);
        return (T) ReflectionUtils.getField(field,target);
    }

    @Override
    public int keySize() {
        return target.getClass().getDeclaredFields().length;
    }
}
