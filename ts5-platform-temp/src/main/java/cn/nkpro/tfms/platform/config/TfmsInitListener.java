package cn.nkpro.tfms.platform.config;

import cn.nkpro.tfms.platform.elasticearch.SearchEngine;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.tfms.platform.model.index.IndexDocItem;
import cn.nkpro.tfms.platform.model.index.IndexPartner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TfmsInitListener implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Autowired
    private SearchEngine searchEngine;


    private void startup() throws IOException {
//        searchEngine.deleteIndices(IndexDoc.class);
//        searchEngine.deleteIndices(IndexDocItem.class);
//        searchEngine.deleteIndices(IndexPartner.class);
        searchEngine.createIndices(IndexDoc.class);
        searchEngine.createIndices(IndexDocItem.class);
        searchEngine.createIndices(IndexPartner.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.startup();
    }
}
