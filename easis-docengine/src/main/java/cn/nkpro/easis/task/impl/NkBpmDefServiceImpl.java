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
package cn.nkpro.easis.task.impl;

import cn.nkpro.easis.exception.NkDefineException;
import cn.nkpro.easis.platform.DeployAble;
import cn.nkpro.easis.task.NkBpmDefService;
import cn.nkpro.easis.task.model.ResourceDefinition;
import cn.nkpro.easis.task.model.ResourceDeployment;
import cn.nkpro.easis.utils.BeanUtilz;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Order(40)
@Service
public class NkBpmDefServiceImpl implements NkBpmDefService, DeployAble {

    @Autowired
    private ProcessEngine processEngine;

    @Override
    public ResourceDeployment deploy(ResourceDefinition definition){

        ProcessDefinition existsDefinition = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(definition.getKey())
                .latestVersion()
                .singleResult();

        // 判断key是否允许覆盖
        if(existsDefinition!=null && (definition.getFromId()==null || !StringUtils.equals(definition.getFromId().split(":")[0],definition.getKey()))){
            throw new NkDefineException(String.format("流程定义[%s]已经存在，请从历史版本进行创建",definition.getKey()));
        }

        return BeanUtilz.copyFromObject(
                processEngine.getRepositoryService()
                    .createDeployment()
                    .addString(definition.getResourceName(),definition.getXml())
                    .deploy(),
                ResourceDeployment.class
        );
    }

    @Override
    public ResourceDefinition getProcessDefinition(String definitionId){

        ResourceDefinition definition = BeanUtilz.copyFromObject(processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult(), ResourceDefinition.class);

        definition.setXml(getBpmnXml(definition.getDeploymentId(),definition.getResourceName()));

        return definition;
    }

    @Override
    public ResourceDefinition getDmnDefinition(String definitionId){

        ResourceDefinition definition = BeanUtilz.copyFromObject(processEngine.getRepositoryService()
                .createDecisionDefinitionQuery()
                .decisionDefinitionId(definitionId)
                .singleResult(), ResourceDefinition.class);

        definition.setXml(getBpmnXml(definition.getDeploymentId(),definition.getResourceName()));

        return definition;
    }

    private String getBpmnXml(String deploymentId, String resourceName) {

        InputStream inputStream = processEngine.getRepositoryService()
                .getResourceAsStream(deploymentId, resourceName);
        InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        try{
            StringBuilder buffer = new StringBuilder();
            String line;
            while((line=br.readLine())!=null) {
                buffer.append(line);
                buffer.append('\n');
            }
            return buffer.toString();
        }catch (IOException ignored){
            return null;
        }finally {
            try {
                br.close();
                isr.close();
            } catch (IOException e) {
                //
            }
        }
    }

    @Override
    public void loadExport(JSONArray exports) {

        JSONObject export = new JSONObject();
        export.put("key","bpmDefs");
        export.put("name","工作流");
        exports.add(export);

        ProcessDefinitionQuery query = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .latestVersion();

        List<ProcessDefinition> definitions = new ArrayList<>();
        long count = query.count();
        int from = 0;
        int rows = 1000;
        do{
            definitions.addAll(query.listPage(from,rows));
            from+=rows;
        }while(from < count);

        export.put("list",definitions.stream()
                .map(definition -> {
                    Map<String,Object> map = new HashMap<>();

                    map.put("key",definition.getId());
                    map.put("name",definition.getKey() + " | " + definition.getName());

                    return map;
                })
                .collect(Collectors.toList()));
    }

    @Override
    public void exportConfig(JSONObject config, JSONObject export) {
        if(config.getJSONArray("bpmDefs")!=null){
            export.put("bpmDefs",
                    config.getJSONArray("bpmDefs").stream().map(definitionId->
                            getProcessDefinition((String) definitionId)
                    ).collect(Collectors.toList())
            );
        }
    }

    @Override
    public void importConfig(JSONObject data) {
        if(data.containsKey("bpmDefs")){
            data.getJSONArray("bpmDefs").toJavaList(ResourceDefinition.class)
                    .forEach(this::deploy);
        }
    }

}
