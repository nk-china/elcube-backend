package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.ts5.exception.TfmsIllegalContentException;
import cn.nkpro.tfms.platform.services.TfmsDefBpmService;
import cn.nkpro.tfms.platform.services.TfmsDefDeployAble;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TfmsDefBpmServiceImpl implements TfmsDefBpmService, TfmsDefDeployAble {

    @Autowired
    private ProcessEngine processEngine;


    @Override
    public Deployment deploy(
            String definitionId,
            String key,
            String resourceName,
            String bpmnXml){

        ProcessDefinition definitionByKey = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(key)
                .latestVersion()
                .singleResult();

        // 判断key是否允许覆盖
        if(definitionByKey!=null && (definitionId==null || !StringUtils.equals(definitionId.split(":")[0],key))){
            throw new TfmsIllegalContentException(String.format("流程定义[%s]已经存在，请从历史版本进行创建",key));
        }

        return processEngine.getRepositoryService()
                .createDeployment()
                .addString(resourceName,bpmnXml)
                .deploy();
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

    @Override
    public String getBpmnXml(String deploymentId, String resourceName) throws IOException {

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
        } finally {
            try {
                br.close();
                isr.close();
            } catch (IOException e) {
                //
            }
        }
    }

    @Override
    public int deployOrder() {
        return 0;
    }

    @Override
    public Object deployExport(JSONObject config) {

        JSONArray array = config.getJSONArray("bpmDefs");

        if(array!=null && array.size()>0){
            return array.stream()
                    .map(item->{
                        JSONObject jsonObject = (JSONObject) item;
                        try {
                            jsonObject.put("bpmnXml",getBpmnXml(jsonObject.getString("deploymentId"),jsonObject.getString("resourceName")));
                            return jsonObject;
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public void deployImport(Object data) {
        if(data!=null)
            ((JSONArray)data).forEach(item->{
                JSONObject jsonObject = (JSONObject) item;
                deploy(null,jsonObject.getString("key"),jsonObject.getString("resourceName"),jsonObject.getString("bpmnXml"));
            });
    }
}
