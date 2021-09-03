package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.docengine.service.NkBpmTaskService;
import cn.nkpro.ts5.docengine.service.SequenceSupport;
import cn.nkpro.ts5.docengine.service.impl.DefaultBpmTaskServiceImpl;
import cn.nkpro.ts5.docengine.service.impl.DefaultSequenceSupportImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocEngineAutoConfiguration implements ApplicationRunner {

    @Autowired
    @SuppressWarnings("all")
    private SearchService searchService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        searchService.init();
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


    @ConditionalOnMissingBean
    @Bean
    public NkBpmTaskService nkBpmTaskService(){
        return new DefaultBpmTaskServiceImpl();
    }
}
