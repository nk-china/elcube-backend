package cn.nkpro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by bean on 2019/12/18.
 */
@EnableScheduling
@EnableTransactionManagement
@MapperScan(basePackages = {"cn.nkpro.ts5.orm.mb"})
@SpringBootApplication(scanBasePackages = {"cn.nkpro"})
public class TfmsPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(TfmsPlatformApplication.class, args);
    }
}
