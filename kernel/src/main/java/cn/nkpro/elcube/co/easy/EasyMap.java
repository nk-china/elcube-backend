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

import java.util.Map;

public class EasyMap implements EasySingle {

    private Map<Object,Object> target;

    @Override
    public Object target() {
        return target;
    }

    EasyMap(Map<Object, Object> target) {
        Assert.notNull(target,"目标数据不能为null");
        this.target = target;
    }

    @Override
    public EasyMap set(String key, Object value){
        target.put(key, value);
        return this;
    }

    @SuppressWarnings("all")
    @Override
    public <T> T get(String key){
        return (T) target.get(key);
    }

    @Override
    public int keySize(){
        return target.size();
    }
}
