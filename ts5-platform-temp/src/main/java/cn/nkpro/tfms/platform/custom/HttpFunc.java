package cn.nkpro.tfms.platform.custom;

import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * Created by bean on 2020/8/12.
 */
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface HttpFunc {
}
