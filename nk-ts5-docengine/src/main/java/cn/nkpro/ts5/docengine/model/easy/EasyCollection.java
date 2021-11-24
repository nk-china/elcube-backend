package cn.nkpro.ts5.docengine.model.easy;

import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("all")
public interface EasyCollection extends Collection<EasySingle> {

    EasySingle append(int index);

    EasySingle append();

    EasySingle find(Function<EasySingle, Boolean> function);

    EasySingle get(int index);

    public static EasyCollection from(Object target){
        Assert.notNull(target,"目标数据不能为null");
        Assert.isTrue((target instanceof List || target.getClass().isArray()),"目标数据必须是 Arrays 或 List 子集");

        if(target instanceof Collection){
            return new EasyList((List<Object>) target);
        }else{
            return new EasyArray((Object[]) target);
        }
    }
}
