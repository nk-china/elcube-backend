/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.platform.service;

import cn.nkpro.easis.co.NkCustomObjectManager;
import cn.nkpro.easis.co.meter.NkMeter;
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
