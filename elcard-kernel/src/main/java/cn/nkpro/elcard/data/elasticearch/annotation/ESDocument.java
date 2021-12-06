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
package cn.nkpro.elcard.data.elasticearch.annotation;

import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ESDocument {

    //索引库名称
    String value();

    //类型
    String type() default "";

    //
    boolean useServerConfiguration() default false;

    //默认分片数5
    short shards() default 1;

    //默认副本数1
    short replicas() default 1;

    //刷新间隔
    String refreshInterval() default "1s";

    //索引文件存储类型
    String indexStoreType() default "fs";

    //是否创建索引
    boolean createIndex() default true;
}