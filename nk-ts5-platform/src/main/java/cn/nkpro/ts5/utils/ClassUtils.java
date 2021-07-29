package cn.nkpro.ts5.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by bean on 2020/8/3.
 */
public interface ClassUtils {

    static String decapitateClassName(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    static String decapitateBeanName(Class<?> clazz){
        String beanName = decapitateClassName(clazz.getSimpleName());

        Component component = clazz.getDeclaredAnnotation(Component.class);
        if(component!=null && StringUtils.isNotBlank(component.value())){
            beanName = component.value();
        }
        Service service = clazz.getDeclaredAnnotation(Service.class);
        if(service!=null && StringUtils.isNotBlank(service.value())){
            beanName = service.value();
        }

        return beanName;
    }

    static boolean hasInterface(Class<?> clazz, Class<?> targetInterface){
        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> i : interfaces){
            if(i==targetInterface)
                return true;
        }

        clazz = clazz.getSuperclass();
        if(clazz == null || clazz == Object.class)
            return false;

        return hasInterface(clazz,targetInterface);
    }
}
