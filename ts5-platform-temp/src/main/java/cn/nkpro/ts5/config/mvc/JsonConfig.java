package cn.nkpro.ts5.config.mvc;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(JsonConfigs.class)
public @interface JsonConfig {
    Class<?> type();
    String[] includes() default {};
    String[] excludes() default {};
}