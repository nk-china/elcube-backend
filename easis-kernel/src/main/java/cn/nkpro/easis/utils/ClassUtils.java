/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.utils;

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
