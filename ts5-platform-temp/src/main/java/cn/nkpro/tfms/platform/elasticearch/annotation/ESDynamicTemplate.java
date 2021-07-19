package cn.nkpro.tfms.platform.elasticearch.annotation;

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

    /**
     * If no match_mapping_type has been specified but the filetemp is valid for at least one predefined mapping type,
     * the mapping snippet is considered valid.
     * However, a validation error is returned at index time if a field matching the filetemp is indexed as a different type.
     * For example, configuring a dynamic filetemp with no match_mapping_type is considered valid as string type,
     * but if a field matching the dynamic filetemp is indexed as a long, a validation error is returned at index time.
     * @return
     */
    String matchMappingType() default "*";

    String match() default "*";

    String unmatch() default "";

    ESFieldType mappingType() default ESFieldType.Keyword;

    /**
     * 指定字段使用搜索时的分词
     */
    ESAnalyzerType searchAnalyzer() default ESAnalyzerType.none;

    /**
     * 指定字段使用的分词
     * @return
     */
    ESAnalyzerType analyzer() default ESAnalyzerType.none;

    ESField[] fields() default {};

    boolean copyToKeyword() default false;
}
