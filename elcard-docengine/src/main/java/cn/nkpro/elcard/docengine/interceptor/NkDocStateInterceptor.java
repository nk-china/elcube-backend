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
package cn.nkpro.elcard.docengine.interceptor;


import cn.nkpro.elcard.co.NkCustomObject;
import cn.nkpro.elcard.docengine.model.DocHV;

/**
 *
 * <b>注意：当前接口尚未实际应用
 *
 * <h3>状态变化拦截器
 * <p>这个拦截器用于处理单据状态变化事件
 * <p>当一个单据被保存时，如果 {@link DocHV#getDocState()} 值发生变化，将触发 {@link #apply()} 方法
 *
 * @author bean 2021-12-03
 * @since v2.0
 *
 */
public interface NkDocStateInterceptor extends NkCustomObject {
    default void apply(){}
}
