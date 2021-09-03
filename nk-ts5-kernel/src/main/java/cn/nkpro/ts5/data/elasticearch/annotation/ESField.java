package cn.nkpro.ts5.data.elasticearch.annotation;

import cn.nkpro.ts5.data.elasticearch.ESAnalyzerType;
import cn.nkpro.ts5.data.elasticearch.ESFieldType;

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