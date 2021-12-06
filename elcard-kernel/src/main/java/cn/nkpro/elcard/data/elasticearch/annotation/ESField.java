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

import cn.nkpro.elcard.data.elasticearch.ESAnalyzerType;
import cn.nkpro.elcard.data.elasticearch.ESFieldType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@Inherited
public @interface ESField {

    /**
     * 字段名称，默认为类字段名称
     */
    String value() default "";

    /**
     * 数据类型
     */
    ESFieldType type() default ESFieldType.Auto;

    /**
     * 是否启用索引
     */
    boolean index() default true;

    /**
     * 是否复制到全局检索
     */
    boolean copyToKeyword() default false;

    /**
     * 暂不可用，默认情况下不存储
     */
    @Deprecated
    boolean store() default false;

    /**
     * 暂不可用，还没有研究明白
     */
    @Deprecated
    boolean fieldData() default false;

    /**
     * 分词后，是否保留原始值用于聚合计算或排序
     * <p>对于分词字段，如果需要排序或者聚合计算的话，需要设置original为true，排序时指定排序字段为 字段名.original
     */
    boolean original() default false;

    /**
     * 指定字段使用的分词
     */
    ESAnalyzerType analyzer() default ESAnalyzerType.none;
    /**
     * 指定字段使用搜索时的分词
     */
    ESAnalyzerType searchAnalyzer() default ESAnalyzerType.none;

    String format() default "";

    /**
     * 暂不可用，如果某个字段需要被忽略
     */
    @Deprecated
    String[] ignoreFields() default {};

    /**
     * 暂不可用
     */
    @Deprecated
    boolean includeInParent() default false;
}