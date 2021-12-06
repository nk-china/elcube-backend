/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.co.query;

import cn.nkpro.elcube.co.NkCustomObjectManager;
import cn.nkpro.elcube.co.query.model.DataSourceDef;
import cn.nkpro.elcube.exception.NkDefineException;
import cn.nkpro.elcube.platform.service.PlatformRegistryService;
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
