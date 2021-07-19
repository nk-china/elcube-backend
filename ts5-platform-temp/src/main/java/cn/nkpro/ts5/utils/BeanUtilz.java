package cn.nkpro.ts5.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bean on 2020/1/3.
 */
public class BeanUtilz {
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
    @FunctionalInterface
    public interface Function<T>{
        void apply(T source);
    }
}
