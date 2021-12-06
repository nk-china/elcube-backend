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
package cn.nkpro.elcard.co;


/**
 * 自定义脚本对象的基础接口
 *
 * 需要注意的是，实现该接口的，并非都是脚本对象
 *
 * 是否是脚本对象需要通过{@link #getScriptDef()}的scriptType属性进行判断
 *
 */
public interface NkCustomScriptObject extends NkCustomObject {
    NkScriptV getScriptDef();
}
