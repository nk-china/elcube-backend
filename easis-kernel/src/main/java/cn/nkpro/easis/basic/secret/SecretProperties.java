package cn.nkpro.easis.basic.secret;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.lang.annotation.Annotation;

@ConfigurationProperties(prefix = "nk.secret")
@Data
public class SecretProperties {
    /**
     * 是否开启
     */
    private boolean enabled;
    /**
     * 是否扫描注解
     */
    private boolean scanAnnotation;
    /**
     * 扫描自定义注解
     */
    private Class<? extends Annotation> annotationClass = SecretBody.class;

    /**
     * 3des 密钥长度不得小于24
     */
    private String desSecretKey = "b2c17b46e2b1415392aab5a82869856c";
    /**
     * 3des IV向量必须为8位
     */
    private String desIv = "61960842";

}