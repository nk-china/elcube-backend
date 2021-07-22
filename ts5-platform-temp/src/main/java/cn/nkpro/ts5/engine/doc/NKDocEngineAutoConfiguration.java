package cn.nkpro.ts5.engine.doc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NKDocEngineAutoConfiguration {

    @Bean
    public NkDocEngine nkDocEngine(){
        return new NkDocEngine();
    }
    @Bean
    public NKDocDefManager nkDocDefManager(){
        return new NKDocDefManager();
    }
}
