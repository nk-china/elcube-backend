package cn.nkpro.ts5.docengine.utils;

import cn.nkpro.ts5.co.easy.EasyCollection;
import cn.nkpro.ts5.co.easy.EasySingle;
import cn.nkpro.ts5.exception.NkSystemException;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CopyUtils {

    static void copy(Object single, Map<String, Object> target, List<String> targetFields){
        if(single != null && !(single instanceof Collection || single.getClass().isArray())){
            EasySingle from = EasySingle.from(single);
            targetFields.forEach(field-> target.put(field, from.get(field)));
        }
    }

    @SuppressWarnings("unchecked")
    static void copy(Object collection, List target, Class<?> targetType, List<String> targetFields){
        if(collection != null && (collection instanceof Collection || collection.getClass().isArray())){
            EasyCollection from = EasyCollection.from(collection);

            from.forEach(item->{
                try {
                    Object instance = targetType.getDeclaredConstructor().newInstance();

                    EasySingle singleTarget = EasySingle.from(instance);

                    targetFields.forEach(field-> singleTarget.set(field, item.get(field)));

                    target.add(instance);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new NkSystemException(e.getMessage(),e);
                }
            });
        }
    }
}
