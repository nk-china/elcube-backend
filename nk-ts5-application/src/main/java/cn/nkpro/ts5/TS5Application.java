package cn.nkpro.ts5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by bean on 2019/12/18.
 */
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@ComponentScan(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@SpringBootApplication(scanBasePackages = {"cn.nkpro.ts5","cn.nkpro.groovy"})
public class TS5Application {
    public static void main(String[] args) {
        SpringApplication.run(TS5Application.class, args);
    }
}
