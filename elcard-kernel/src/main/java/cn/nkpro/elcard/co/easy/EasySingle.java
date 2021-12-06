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
package cn.nkpro.elcard.co.easy;

import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;

@SuppressWarnings("all")
public interface EasySingle {

    Object target();

    EasySingle set(String key, Object value);

    <T> T get(String key);

    int keySize();

    public static EasySingle from(Object target){
        Assert.notNull(target,"目标数据不能为null");
        Assert.isTrue(!(target instanceof Collection || target.getClass().isArray()),"目标数据必须是 Entity 或 Map 子集");

        if(target instanceof Map){
            return new EasyMap((Map<Object, Object>) target);
        }
        return new EasyEntity(target);
    }
}
