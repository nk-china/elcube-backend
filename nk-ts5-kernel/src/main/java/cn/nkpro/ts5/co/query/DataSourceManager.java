package cn.nkpro.ts5.co.query;

import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.exception.NkDefineException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataSourceManager {

    @SuppressWarnings("all")
    @Autowired
    private NkCustomObjectManager customObjectManager;

    private Map<String,DataSourceDef> dataSourceDefs;

    @AllArgsConstructor
    @Data
    public static
    class DataSourceDef{
        private String name;
        private String type;
        private String service;
    }

    public DataSourceManager(){
        dataSourceDefs = new HashMap<>();
        dataSourceDefs.put(null, new DataSourceDef(null, "ElasticSearch", "ElasticSearchService"));
        dataSourceDefs.put("document", new DataSourceDef("document", "ElasticSearch", "ElasticSearchService"));
        dataSourceDefs.put("doc-ext", new DataSourceDef("doc-ext", "ElasticSearch", "ElasticSearchService"));
        dataSourceDefs.put("document-custom", new DataSourceDef("document-custom", "ElasticSearch", "ElasticSearchService"));
        dataSourceDefs.put("hits_v1", new DataSourceDef("hits_v1", "ClickHouse", "ClickHouseService"));
    }

    public Collection<DataSourceDef> getDataSources(){
        return dataSourceDefs.values().stream().filter(e->e.getName()!=null).sorted(Comparator.comparing(DataSourceDef::getName)).collect(Collectors.toList());
    }

    public DataQueryService getService(String datasourceName){

        datasourceName = datasourceName.replaceAll("(^\")|(\"$)","");
        DataSourceDef def = dataSourceDefs.get(datasourceName);
        if(def==null){
            throw new NkDefineException("数据源没有找到");
        }

        return customObjectManager.getCustomObject(def.getService(), DataQueryService.class);
    }
}
