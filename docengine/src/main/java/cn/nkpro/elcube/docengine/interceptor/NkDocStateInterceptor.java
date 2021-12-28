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
package cn.nkpro.elcube.docengine.interceptor;


import cn.nkpro.elcube.co.NkCustomObject;
import cn.nkpro.elcube.docengine.gen.DocDefState;
import cn.nkpro.elcube.docengine.model.DocHV;

/**
 * *
 * <h3>状态变化拦截器</h3>
 * <p>这个拦截器用于处理状态可见行</p>
 * <p>返回true则状态可见</p>
 *
 * @author bean 2021-12-03
 * @since v2.0
 *
 */
public interface NkDocStateInterceptor extends NkCustomObject {
    default boolean apply(DocHV doc, DocDefState state){return true;}
}
