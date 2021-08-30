package cn.nkpro.ts5.engine.elasticearch.annotation;

import cn.nkpro.ts5.engine.elasticearch.ESAnalyzerType;
import cn.nkpro.ts5.engine.elasticearch.ESFieldType;
import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * Created by bean on 2020/8/4.
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(ESDynamicTemplates.class)
public @interface ESDynamicTemplate {

    //模版名称
    String value();

    String matchMappingType() default "*";

    String match() default "*";

    String unmatch() default "";

    ESFieldType mappingType() default ESFieldType.Keyword;

    /**
     * 分词
     */
    ESAnalyzerType analyzer() default ESAnalyzerType.none;
    /**
     * 搜索时的分词
     */
    ESAnalyzerType searchAnalyzer() default ESAnalyzerType.none;

    String format() default "";

    ESField[] fields() default {};

    boolean copyToKeyword() default false;
    /**
     * 分词后，是否保留原始值用于聚合计算或排序
     * <p>对于分词字段，如果需要排序或者聚合计算的话，需要设置original为true，排序时指定排序字段为 字段名.original
     */
    boolean original() default false;
}
