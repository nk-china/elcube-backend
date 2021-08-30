package cn.nkpro.ts5.engine.elasticearch;

import cn.nkpro.ts5.engine.elasticearch.model.BpmTaskES;
import cn.nkpro.ts5.engine.elasticearch.model.DocHES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ESManager {

    @Autowired
    @SuppressWarnings("all")
    private SearchEngine searchEngine;

    public void init() throws IOException {

        searchEngine.createIndices(DocHES.class);
        searchEngine.createIndices(BpmTaskES.class);
    }

    public void dropAndInit() throws IOException {

        searchEngine.deleteIndices(DocHES.class);
        searchEngine.deleteIndices(BpmTaskES.class);

        this.init();
    }
}
