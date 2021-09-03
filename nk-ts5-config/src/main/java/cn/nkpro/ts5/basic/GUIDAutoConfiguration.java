package cn.nkpro.ts5.basic;

import cn.nkpro.ts5.Keep;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Keep
@Configuration
public class GUIDAutoConfiguration {


    /**
     * ID序列配置 默认UUID
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public GUID guid(){
        return new GUID(){};
    }

}
