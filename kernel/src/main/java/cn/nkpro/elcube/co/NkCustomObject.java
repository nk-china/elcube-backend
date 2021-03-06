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
package cn.nkpro.elcube.co;

import cn.nkpro.elcube.annotation.NkNote;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.Order;

/**
 *
 * 自定义对象的基本接口，所有的自定义对象均需要实现该接口
 *
 * Created by bean on 2020/7/16.
 */
public interface NkCustomObject {

    default String getBeanName(){return getClass().getName();}

    default boolean isFinal(){
        return false;
    }

    default String desc() {

        NkNote nkNote = this.getClass().getAnnotation(NkNote.class);
        if(nkNote!=null)
            return getBeanName() + " | " + nkNote.value();

        return getBeanName();
    }

    default int order(){
        Order order = AopUtils.getTargetClass(this).getAnnotation(Order.class);
        if(order!=null){
            return order.value();
        }
        return Integer.MAX_VALUE;
    }
}
