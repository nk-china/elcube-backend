package cn.nkpro.tfms.platform.bpm;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;


@Configuration
public class TfmsSpringProcessEngineConfiguration implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public TfmsFlowableEventListener defaultFlowableEventListener(){
        return new TfmsFlowableEventListener();
    }

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setTransactionManager(transactionManager);
        engineConfiguration.setEventListeners(Collections.singletonList(defaultFlowableEventListener()));
    }
}
