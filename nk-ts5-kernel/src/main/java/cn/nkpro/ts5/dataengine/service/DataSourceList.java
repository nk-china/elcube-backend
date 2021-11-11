package cn.nkpro.ts5.dataengine.service;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


public class DataSourceList {
    @Getter
    private Map<String,String> list;

    public DataSourceList(){
        list = new HashMap<>();

        list.put("document","ElasticSearchService");
        list.put("doc-ext","ElasticSearchService");
        list.put("document-custom","ElasticSearchService");
        list.put("hits_v1","ClickHouseService");
    }
}
