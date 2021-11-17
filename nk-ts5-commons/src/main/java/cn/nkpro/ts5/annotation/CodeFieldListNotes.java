package cn.nkpro.ts5.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 泛型注解
 * 
 * @author Bean
 * @create 2015年12月24日 上午1:36:29
 * @modify
 * @version
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeFieldListNotes {
	/**
	 * 字段描述
	 * @return
	 */
	String value();

	/**
	 * 组件类型
	 * @return
	 */
	Class<?> componentType() default Object.class;
}
