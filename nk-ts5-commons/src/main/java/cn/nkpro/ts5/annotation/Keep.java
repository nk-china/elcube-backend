package cn.nkpro.ts5.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER,ElementType.FIELD,ElementType.METHOD,ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface Keep {
}
