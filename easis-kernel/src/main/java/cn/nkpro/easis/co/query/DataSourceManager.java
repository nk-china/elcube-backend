package cn.nkpro.easis.co.query;

import cn.nkpro.easis.co.NkCustomObjectManager;
import cn.nkpro.easis.co.query.model.DataSourceDef;
import cn.nkpro.easis.exception.NkDefineException;
import cn.nkpro.easis.platform.service.PlatformRegistryService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class DataSourceManager {

    @SuppressWarnings("all")
    @Autowired
    private NkCustomObjectManager customObjectManager;

    @SuppressWarnings("all")
    @Autowired
    private PlatformRegistryService registryService;

    private static final String REG_KEY = "@DATASET";



    public Collection<DataSourceDef> getDataSources(){
        return registryService.getAllByType(REG_KEY).stream()
                .map(registry -> {
                    DataSourceDef def = JSON.parseObject(registry.getContent(),DataSourceDef.class);
                    def.setName(registry.getRegKey());
                    return def;
                })
                .sorted(Comparator.comparing(DataSourceDef::getName))
                .collect(Collectors.toList());
    }

    public DataQueryService getService(String datasourceName){

        datasourceName = datasourceName.replaceAll("(^\")|(\"$)","");

        JSONObject def = (JSONObject) registryService.getJSON(REG_KEY, datasourceName);

        if(def==null){
            throw new NkDefineException("数据源没有找到");
        }

        return customObjectManager.getCustomObject(def.getString("service"), DataQueryService.class);
    }
}
