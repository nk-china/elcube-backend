package cn.nkpro.easis.bigdata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "cn.nkpro.ts5.bigdata.gen",
})
public class NkBigDataConfig {
}
