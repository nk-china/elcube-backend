package cn.nkpro.ts5.engine.bpm.impl;

import cn.nkpro.ts5.engine.bpm.NkBpmDefService;
import cn.nkpro.ts5.engine.bpm.model.DeploymentV;
import cn.nkpro.ts5.engine.bpm.model.ProcessDefinitionV;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class NkBpmDefServiceImpl implements NkBpmDefService {

    @Autowired
    private ProcessEngine processEngine;

    @Override
    public DeploymentV deploy(ProcessDefinitionV definition){

        ProcessDefinition existsDefinition = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(definition.getKey())
                .latestVersion()
                .singleResult();

        // 判断key是否允许覆盖
        if(existsDefinition!=null && (definition.getFromId()==null || !StringUtils.equals(definition.getFromId().split(":")[0],definition.getKey()))){
            throw new TfmsException(String.format("流程定义[%s]已经存在，请从历史版本进行创建",definition.getKey()));
        }

        return BeanUtilz.copyFromObject(
                processEngine.getRepositoryService()
                    .createDeployment()
                    .addString(definition.getResourceName(),definition.getBpmnXml())
                    .deploy(),
                DeploymentV.class
        );
    }

    @Override
    public ProcessDefinitionV getProcessDefinition(String definitionId){
        ProcessDefinitionV definition = BeanUtilz.copyFromObject(processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult(),ProcessDefinitionV.class);

        definition.setBpmnXml(getBpmnXml(definition.getDeploymentId(),definition.getResourceName()));

        return definition;
    }

    @Override
    public List<Object> getAllDeployments(){
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
        return definitions.stream()
                .map(definition -> {
                    Map<String,Object> map = new HashMap<>();

                    map.put("key",definition.getKey());
                    map.put("name",definition.getName());
                    map.put("deploymentId",definition.getDeploymentId());
                    map.put("resourceName",definition.getResourceName());

                    return map;
                })
                .collect(Collectors.toList());
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

//    @Override
//    public int deployOrder() {
//        return 0;
//    }
//
//    @Override
//    public Object deployExport(JSONObject config) {
//
//        JSONArray array = config.getJSONArray("bpmDefs");
//
//        if(array!=null && array.size()>0){
//            return array.stream()
//                    .map(item->{
//                        JSONObject jsonObject = (JSONObject) item;
//                        try {
//                            jsonObject.put("bpmnXml",getBpmnXml(jsonObject.getString("deploymentId"),jsonObject.getString("resourceName")));
//                            return jsonObject;
//                        } catch (IOException e) {
//                            return null;
//                        }
//                    })
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//        }
//
//        return Collections.emptyList();
//    }
//
//    @Override
//    public void deployImport(Object data) {
//        if(data!=null)
//            ((JSONArray)data).forEach(item->{
//                JSONObject jsonObject = (JSONObject) item;
//                deploy(jsonObject.getString("key")+":"+jsonObject.getString("deploymentId"),jsonObject.getString("key"),jsonObject.getString("resourceName"),jsonObject.getString("bpmnXml"));
//            });
//    }
}
