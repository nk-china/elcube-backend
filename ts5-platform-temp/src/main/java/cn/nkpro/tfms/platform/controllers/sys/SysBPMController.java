package cn.nkpro.tfms.platform.controllers.sys;

import cn.nkpro.tfms.platform.model.util.PageList;
import cn.nkpro.ts5.config.mvc.JsonConfig;
import cn.nkpro.tfms.platform.services.TfmsDefBpmService;
import cn.nkpro.tfms.platform.services.TfmsDocService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("S5.工作流配置")
@Controller
@RequestMapping("/def/bpm")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:BPM')")
public class SysBPMController {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private TfmsDocService docService;
    @Autowired
    private TfmsDefBpmService defBpmService;

    @WsDocNote("11、部署流程")
    @RequestMapping(value = "/deploy",method = RequestMethod.POST)
    @JsonConfig(type = Deployment.class, excludes = {"resources"})
    public Deployment deploy(
            @WsDocNote("来源流程定义ID")@RequestParam(value = "definitionId",required = false)String definitionId,
            @WsDocNote("流程定义KEY")@RequestParam("key")String key,
            @WsDocNote("资源文件名称")@RequestParam("resourceName")String resourceName,
            @WsDocNote("XML文本")@RequestParam("bpmnXml")String bpmnXml){
        return defBpmService.deploy(definitionId,key,resourceName,bpmnXml);
    }

    @WsDocNote("12、拉取部署记录")
    @RequestMapping(value = "/deployments")
    @JsonConfig(type = Deployment.class, excludes = {"resources"})
    public PageList<Deployment> deployments(
            @WsDocNote("起始条目")@RequestParam("from") Integer from,
            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){

        DeploymentQuery query = processEngine.getRepositoryService()
                .createDeploymentQuery();

        return new PageList<>(
                query.orderByDeploymentTime().desc()
                        .orderByDeploymentName().desc()
                        .listPage(from,rows),
                from,
                rows,
                query.count());
    }

    @WsDocNote("21、拉取实例定义")
    @RequestMapping(value = "/process/definitions")
    @JsonConfig(type = ProcessDefinition.class, excludes = {"identityLinks"})
    public PageList<ProcessDefinition> processDefinitions(
            @WsDocNote("查询条目")@RequestParam(value = "latest",       required = false, defaultValue = "false") Boolean latest,
            @WsDocNote("查询条目")@RequestParam(value = "keyword",      required = false) String key,
            @WsDocNote("查询条目")@RequestParam(value = "orderField",   required = false) String orderField,
            @WsDocNote("查询条目")@RequestParam(value = "order",        required = false) String order,
            @WsDocNote("起始条目")@RequestParam("from") Integer from,
            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){

        ProcessDefinitionQuery query = processEngine.getRepositoryService()
                .createProcessDefinitionQuery();

        if(latest)
            query.latestVersion();
        if(StringUtils.isNotBlank(key))
            query.processDefinitionKeyLike(String.format("%%%s%%",key));
        if(StringUtils.isNotBlank(orderField)){
            switch (orderField){
                case "key":
                    query.orderByProcessDefinitionKey();
                    if("desc".equalsIgnoreCase(order))
                        query.desc();
                    else
                        query.asc();
                    break;
                case "name":
                    query.orderByProcessDefinitionName();
                    if("desc".equalsIgnoreCase(order))
                        query.desc();
                    else
                        query.asc();
                    break;
                case "version":
                    query.orderByProcessDefinitionVersion();
                    if("desc".equalsIgnoreCase(order))
                        query.desc();
                    else
                        query.asc();
                    break;
            }
        }

        query.listPage(from,rows).forEach(processDefinition -> {
            System.out.println(processDefinition.getName());
        });

        return new PageList<>(
            query.listPage(from,rows),
            from,
            rows,
            query.count());
    }

    @WsDocNote("22、拉取实例定义详情")
    @RequestMapping(value = "/process/definition/detail")
    @JsonConfig(type = ProcessDefinition.class, excludes = {"identityLinks"})
    public ProcessDefinition processDefinitionDetail(String definitionId){

        ProcessDefinition definition = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();
        return definition;
    }

    @WsDocNote("23、拉取实例定义模型")
    @RequestMapping(value = "/process/definition/bpmn")
    @ResponseBody
    public JSONObject processDefinitionBpmn(
            @RequestParam("definitionId") String definitionId) throws IOException {

        ProcessDefinition definition = processDefinitionDetail(definitionId);

        JSONObject json = new JSONObject();
        json.put("definitionId",definition.getId());
        json.put("key",definition.getKey());
        json.put("version",definition.getVersion());
        json.put("bpmnXml",defBpmService.getBpmnXml(definition.getDeploymentId(),definition.getResourceName()));

        return json;
    }

    @WsDocNote("24、拉取实例定义资源")
    @RequestMapping(value = "/process/definition/resource")
    @ResponseBody
    public String processDefinitionDetailResource(
            @RequestParam("deplymentId") String deplymentId,
            @RequestParam("resourceName") String resourceName) throws IOException {

        if(resourceName.endsWith(".png")){
            InputStream inputStream = processEngine.getRepositoryService().getResourceAsStream(deplymentId, resourceName);
            try {
                byte[] data = new byte[inputStream.available()];
                inputStream.read(data);
                return Base64.encodeBase64String(data);

            } catch (IOException e) {
                throw e;
            }finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //
                }
            }
        }

        return null;
    }

    @WsDocNote("31、拉取流程实例")
    @RequestMapping(value = "/process/instances")
    @JsonConfig(type = ProcessInstance.class, includes = {
            "id","name","tenantId","activityId","referenceId",
            "referenceType","processInstanceId","processVariables",
            "processDefinitionId","processDefinitionName","processDefinitionKey","processDefinitionVersion",
            "deploymentId",
            "suspended","businessKey","description","localizedName","localizedDescription",
            "startTime","startUserId","callbackId","callbackType"
    })
    public PageList<ProcessInstance> processInstances(
            @WsDocNote("起始条目")@RequestParam("from") Integer from,
            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){

        ProcessInstanceQuery query = processEngine.getRuntimeService()
                .createProcessInstanceQuery();

        return new PageList<>(
                query.orderByStartTime().desc()
                        .listPage(from,rows),
                from,
                rows,
                query.count());
    }

    @WsDocNote("41、强制终止任务")
    @RequestMapping(value = "/process/kill")
    @ResponseBody
    public void processInstanceKill(
            @WsDocNote("任务Id")@RequestParam("instanceId") String instanceId){

        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();

        if(StringUtils.isNotBlank(processInstance.getBusinessKey())){
            docService.onBpmKilled(processInstance.getBusinessKey());
        }

        processEngine.getRuntimeService().deleteProcessInstance(instanceId,"强制结束流程");
    }

    @WsDocNote("51、拉取历史流程实例")
    @RequestMapping(value = "/historic/process/instances")
    @ResponseBody
    public PageList<HistoricProcessInstance> historicProcessInstances(
            @WsDocNote("起始条目")@RequestParam("from") Integer from,
            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){

        HistoricProcessInstanceQuery query = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery();

        return new PageList<>(
                query.orderByProcessInstanceStartTime().desc()
                        .listPage(from,rows),
                from,
                rows,
                query.count());
    }
    @WsDocNote("52、拉取历史任务实例")
    @RequestMapping(value = "/historic/tasks")
    public List<HistoricTaskInstance> historicTasks(
            @WsDocNote("起始条目")@RequestParam("from") Integer from,
            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){
        return processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .orderByHistoricTaskInstanceStartTime().desc()
                .listPage(from,rows);
    }
}
