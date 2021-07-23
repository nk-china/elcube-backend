package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.doc.impl.NkDocEngineImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NKDocEngineAutoConfiguration {

    @Bean
    public NkDocEngineImpl nkDocEngine(){
        return new NkDocEngineImpl();
    }
    @Bean
    public NKDocDefManager nkDocDefManager(){
        return new NKDocDefManager();
    }
}
