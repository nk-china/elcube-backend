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

import cn.nkpro.elcard.exception.NkOperateNotAllowedCaution;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class EasyArray extends EasyList{

    EasyArray(Object[] target) {
        this.easies = Arrays.stream(target).map(EasySingle::from).collect(Collectors.toList());
    }

    @Override
    public EasySingle append(int index){
        throw new NkOperateNotAllowedCaution("数组元数据不支持的添加、移除操作");
    }
    @Override
    public EasySingle append(){
        throw new NkOperateNotAllowedCaution("数组元数据不支持的添加、移除操作");
    }

    @Override
    public boolean add(EasySingle e) {
        throw new NkOperateNotAllowedCaution("不支持的操作");
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends EasySingle> c) {
        throw new NkOperateNotAllowedCaution("不支持的操作");
    }

    @Override
    public boolean remove(Object o) {
        throw new NkOperateNotAllowedCaution("数组元数据不支持的添加、移除操作");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new NkOperateNotAllowedCaution("不支持的操作");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new NkOperateNotAllowedCaution("不支持的操作");
    }

    @Override
    public void clear() {
        throw new NkOperateNotAllowedCaution("数组元数据不支持的添加、移除操作");
    }
}
