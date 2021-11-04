package cn.nkpro.ts5.dataengine.service;

import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.dataengine.meter.NkMeter;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeterService {

    @Autowired
    private NkCustomObjectManager customObjectManager;

    public List<JSONObject> getCardList() {
        return customObjectManager.getCustomObjects(NkMeter.class)
                .entrySet()
                .stream()
                .map(entry->{
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("component",entry.getKey());
                    jsonObject.put("name",entry.getValue().getName());
                    jsonObject.put("w",entry.getValue().getW());
                    jsonObject.put("h",entry.getValue().getH());
                    return jsonObject;
                }).collect(Collectors.toList());
    }

    public Object getCardData(String meterName,Object config){
        return customObjectManager.getCustomObject(meterName, NkMeter.class).getData(config);
    }
}
