package cn.nkpro.easis;

import cn.nkpro.easis.basic.NkProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

/**
 * Created by bean on 2021/9/17.
 */
@SpringBootApplication
@EnableConfigurationProperties(NkProperties.class)
@ComponentScan(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        basePackages = {"cn.nkpro.easis"}
)
public class EasisApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasisApplication.class, args);
    }
}
