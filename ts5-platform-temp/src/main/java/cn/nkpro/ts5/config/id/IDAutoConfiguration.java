package cn.nkpro.ts5.config.id;

import cn.nkpro.ts5.config.id.defaults.DefaultSequenceSupportImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IDAutoConfiguration {


    /**
     * ID序列配置 默认UUID
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public GUID guid(){
        return new GUID(){};
    }
    /**
     * 单据编号序列配置
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public SequenceSupport sequenceSupport(){
        return new DefaultSequenceSupportImpl();
    }

}
