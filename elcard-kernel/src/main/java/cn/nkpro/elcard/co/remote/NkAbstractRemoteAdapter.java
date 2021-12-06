/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.co.remote;

import cn.nkpro.elcard.co.NkAbstractCustomScriptObject;
import cn.nkpro.elcard.utils.BeanUtilz;
import cn.nkpro.elcard.utils.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class NkAbstractRemoteAdapter<R,T> extends NkAbstractCustomScriptObject implements NkRemoteAdapter<R,T> {

    @Override
    public <S> S apply(Object options, Class<S> returnType) {

        if(options!=null){
            Type superclass = getClass().getGenericSuperclass();
            if(superclass instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) superclass).getActualTypeArguments()[0];
                if(rawType instanceof ParameterizedType){
                    rawType = ((ParameterizedType)rawType).getRawType();
                }

                if(!(rawType==options.getClass()||(rawType instanceof Class && ((Class) rawType).isInterface() && ClassUtils.hasInterface(options.getClass(), (Class<?>) rawType)))){
                    System.out.println(rawType);
                    System.out.println(options.getClass());
                    options = BeanUtilz.cloneWithFastjson(options, rawType);
                }
            }
        }

        @SuppressWarnings("all")
        Object apply = this.doApply((R) options);
        return BeanUtilz.cloneWithFastjson(apply, returnType);
    }

    public abstract Object doApply(R options);
}
