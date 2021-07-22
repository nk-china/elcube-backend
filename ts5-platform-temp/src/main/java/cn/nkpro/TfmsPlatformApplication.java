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
@MapperScan(basePackages = {
        "cn.nkpro.tfms.platform.mappers",
        "cn.nkpro.ts5.model.mb",
})
@SpringBootApplication(scanBasePackages = {
        "cn.nkpro"
})
public class TfmsPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(TfmsPlatformApplication.class, args);
        System.out.println(cn.nkpro.ts5.cards.NkCardDate.class);
    }
}
