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
package cn.nkpro.elcube.co.easy;

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
