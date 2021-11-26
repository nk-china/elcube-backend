package cn.nkpro.ts5.co.easy;

import org.springframework.util.Assert;

import java.util.Map;

public class EasyMap implements EasySingle {

    private Map<Object,Object> target;

    @Override
    public Object target() {
        return target;
    }

    EasyMap(Map<Object, Object> target) {
        Assert.notNull(target,"目标数据不能为null");
        this.target = target;
    }

    @Override
    public EasyMap set(String key, Object value){
        target.put(key, value);
        return this;
    }

    @SuppressWarnings("all")
    @Override
    public <T> T get(String key){
        return (T) target.get(key);
    }

    @Override
    public int keySize(){
        return target.size();
    }
}
