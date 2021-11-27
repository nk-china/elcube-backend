package cn.nkpro.easis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

/**
 * Created by bean on 2021/9/17.
 */
@SpringBootApplication
@ComponentScan(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
                basePackages = {
                    "cn.nkpro.easis",
                    "cn.nkpro.groovy"
})
public class TS5Application {
    public static void main(String[] args) {
        SpringApplication.run(TS5Application.class, args);
    }
}
