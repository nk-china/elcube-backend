package cn.nkpro.ts5.dataengine.service;

import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.dataengine.meter.NkMeter;
import cn.nkpro.ts5.platform.gen.PlatformRegistry;
import cn.nkpro.ts5.platform.service.PlatformRegistryService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MeterService {

    @Autowired
    private NkCustomObjectManager customObjectManager;
    @Autowired
    private PlatformRegistryService registryService;

    public List<JSONObject> getCardDefs(){
        return customObjectManager.getCustomObjects(NkMeter.class)
                .entrySet()
                .stream()
                .map(entry -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("component", entry.getKey());
                    jsonObject.put("name", entry.getValue().getName());
                    jsonObject.put("w", entry.getValue().getW());
                    jsonObject.put("h", entry.getValue().getH());
                    return jsonObject;
                }).collect(Collectors.toList());
    }

    public List<JSONObject> getCardList() {

        List<JSONObject> collect = getCardDefs();

        collect.addAll(
            registryService.getAllByType("@METER").stream().map(registry -> {
                    String meterType = registry.getDataType();

                    JSONObject jsonObject = collect.stream().filter(m -> StringUtils.equals(m.getString("component"), meterType))
                            .findFirst().orElse(null);

                    if(jsonObject!=null){
                        JSONObject config = JSON.parseObject(registry.getContent());
                        JSONObject clone = (JSONObject) jsonObject.clone();
                        clone.put("config",config);
                        clone.put("name",registry.getTitle());
                        return clone;
                    }
                    return null;

                }).filter(Objects::nonNull).collect(Collectors.toList())
        );

        return collect;
    }

    public Object getCardData(String meterName,Object config){
        return customObjectManager.getCustomObject(meterName, NkMeter.class).getData(config);
    }
}
