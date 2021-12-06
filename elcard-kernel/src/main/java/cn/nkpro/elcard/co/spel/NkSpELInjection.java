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
package cn.nkpro.elcard.co.spel;

import cn.nkpro.elcard.co.NkCustomObject;

/**
 * 自定义SqEL表达式接口
 *
 * 实现此接口的Bean会被注入到EvaluationContext中，通过@[name]来使用
 *
 * 需要注意的是，实现此接口的Bean的命名需要遵循SpEL+[name]的规则，在SpEL中使用@[name]
 *
 * @see NkSpELManager#createContext(Object)
 *
 */
public interface NkSpELInjection extends NkCustomObject {
}
