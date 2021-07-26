package cn.nkpro.ts5.engine.elasticearch;

import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NKElasticSearchAutoConfigurer implements ApplicationRunner {

    @Autowired@SuppressWarnings("all")
    private SearchEngine searchEngine;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        searchEngine.createIndices(DocHES.class);
    }
}