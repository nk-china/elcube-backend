package cn.nkpro.ts5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by bean on 2019/12/18.
 */
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableProcessApplication
@MapperScan(basePackages = {"cn.nkpro.ts5.orm.mb"})
@SpringBootApplication(scanBasePackages = {"cn.nkpro"})
public class TS5PlatformApplication {
    public static void main(String[] args) {

        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteMapNullValue.getMask();
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
        SpringApplication.run(TS5PlatformApplication.class, args);
    }
}
