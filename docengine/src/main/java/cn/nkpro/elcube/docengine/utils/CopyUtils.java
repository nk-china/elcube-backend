/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.docengine.utils;

import cn.nkpro.elcube.co.easy.EasyCollection;
import cn.nkpro.elcube.co.easy.EasySingle;
import cn.nkpro.elcube.exception.NkSystemException;

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
