package cn.nkpro.ts5.engine.elasticearch.annotation;

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