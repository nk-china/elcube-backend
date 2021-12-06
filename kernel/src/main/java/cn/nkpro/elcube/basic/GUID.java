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
package cn.nkpro.elcube.basic;

import java.util.UUID;

/**
 * 对系统ID生成规则简单包装
 *
 *  事实上这并没有什么用，只是我们遇到有客户强制要求不使用UUID的情况
 *
 *  系统中很多功能并没有完全按照这种方式实现ID规则
 *
 */
public interface GUID {

    default String nextId(Class type) {
        return UUID.randomUUID().toString();
    }
    default String nextId(Class type,String docType){
        return nextId(type);
    }
}
