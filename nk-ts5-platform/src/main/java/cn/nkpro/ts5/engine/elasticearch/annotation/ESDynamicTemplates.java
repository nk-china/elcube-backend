package cn.nkpro.ts5.engine.elasticearch.annotation;

import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * Created by bean on 2020/8/4.
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ESDynamicTemplates {

    //模版名称
    ESDynamicTemplate[] value() default {};
}
