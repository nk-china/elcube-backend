package cn.nkpro.ts5.utils;

import cn.nkpro.ts5.engine.doc.impl.NkDocTransactionProcessor;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/1/3.
 */
public class BeanUtilz {

    public static <T> T cloneWithFastjson(Object source){
        if(source==null)return null;
        return JSON.parseObject(
                JSON.toJSONString(source),
                (Type) source.getClass()
        );
    }

    /**
     * 将source复制到target对象，并返回
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyFromObject(Object source, T target){
        if(source!=null){
            BeanUtils.copyProperties(source,target);
        }
        return target;
    }
    /**
     * 将source复制到一个新的type类型对象，并返回
     * @param source
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T copyFromObject(Object source, Class<T> type){
        if(source!=null){
            T target = BeanUtils.instantiateClass(type);
            BeanUtils.copyProperties(source,target);
            return target;
        }
        return null;
    }
    /***
     * 将source集合复制到一个新的type类型的集合，并返回
     * @param source
     * @param type
     * @param <T>
     * @return
     */
    public static <T> List<T> copyFromList(List<?> source, Class<T> type){
        return copyFromList(source, type, (obj)->{});
    }

    /**
     * 将source集合复制到一个新的type类型的集合，并返回，通知在复制的过程中可以通过mapper接口对复制结果进行二次处理
     * @param source
     * @param type
     * @param mapper
     * @param <T>
     * @return
     */
    public static <T> List<T> copyFromList(List<?> source, Class<T> type, Function<T> mapper){

        return source.stream()
                .map(item->{
                    T target = copyFromObject(item,type);
                    mapper.apply(target);
                    return target;
                })
                .collect(Collectors.toList());
    }

    public static <D> void diffList(List<D> list1, List<D> list2,
                                java.util.function.Function<D,String> key1,
                                java.util.function.Function<D,String> key2,
                                DiffFunction<D> added,
                                DiffFunction<D> updated,
                                DiffFunction<D> removed){

        Map<String,D> map1 = list1!=null?list1.stream().collect(Collectors.toMap(key1, e->e)): Collections.emptyMap();
        Map<String,D> map2 = list2!=null?list2.stream().collect(Collectors.toMap(key2, e->e)): Collections.emptyMap();

        map1.forEach((k,v)->{
            if(map2.containsKey(k)){
                updated.apply(k,v);
            }else{
                added.apply(k,v);
            }
        });
        map2.forEach((k,v)->{
            if(!map1.containsKey(k)){
                removed.apply(k,v);
            }
        });
    }

    @FunctionalInterface
    public interface Function<T>{
        void apply(T source);
    }

    @FunctionalInterface
    public interface DiffFunction<D>{
        public void apply(String key,D data);
    }
}
