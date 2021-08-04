package cn.nkpro.ts5.controller.def;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.task.model.BpmDeployment;
import cn.nkpro.ts5.engine.task.model.BpmProcessDefinition;
import cn.nkpro.ts5.engine.task.NkBpmDefService;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("36.[DevDef]工作流配置")
@RestController
@RequestMapping("/def/bpm")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:BPM')")
public class SysBPMDefController {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private NkBpmDefService defBpmService;

    @WsDocNote("1.拉取定义")
    @RequestMapping(value = "/process/definitions")
    public PageList<BpmProcessDefinition> processDefinitions(
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
                    break;
                case "name":
                    query.orderByProcessDefinitionName();
                    break;
                case "version":
                    query.orderByProcessDefinitionVersion();
                    break;
            }
            if("desc".equalsIgnoreCase(order)){
                query.desc();
            }else {
                query.asc();
            }
        }

        return new PageList<>(
                BeanUtilz.copyFromList(query.listPage(from,rows), BpmProcessDefinition.class),
                from,
                rows,
                query.count());
    }
    @WsDocNote("2.拉取定义详情")
    @RequestMapping(value = "/process/definition/detail")
    public BpmProcessDefinition processDefinitionDetail(String definitionId){
        return defBpmService.getProcessDefinition(definitionId);
    }

    @WsDocNote("3.部署流程定义")
    @RequestMapping(value = "/deploy",method = RequestMethod.POST)
    public BpmDeployment deploy(@RequestBody BpmProcessDefinition definition){
        return defBpmService.deploy(definition);
    }

    @WsDocNote("4.拉取部署记录")
    @RequestMapping(value = "/deployments")
    public PageList<BpmDeployment> deployments(
            @WsDocNote("起始条目")@RequestParam("from") Integer from,
            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){

        DeploymentQuery query = processEngine.getRepositoryService()
                .createDeploymentQuery();

        return new PageList<>(
                BeanUtilz.copyFromList(query.orderByDeploymentTime().desc()
                        .orderByDeploymentName().desc()
                        .listPage(from,rows), BpmDeployment.class),
                from,
                rows,
                query.count());
    }

//
//    @WsDocNote("31、拉取流程实例")
//    @RequestMapping(value = "/process/instances")
//    @JsonConfig(type = ProcessInstance.class, includes = {
//            "id","name","tenantId","activityId","referenceId",
//            "referenceType","processInstanceId","processVariables",
//            "processDefinitionId","processDefinitionName","processDefinitionKey","processDefinitionVersion",
//            "deploymentId",
//            "suspended","businessKey","description","localizedName","localizedDescription",
//            "startTime","startUserId","callbackId","callbackType"
//    })
//    public PageList<ProcessInstance> processInstances(
//            @WsDocNote("起始条目")@RequestParam("from") Integer from,
//            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){
//
//        ProcessInstanceQuery query = processEngine.getRuntimeService()
//                .createProcessInstanceQuery();
//
//        return new PageList<>(
//                query.orderByStartTime().desc()
//                        .listPage(from,rows),
//                from,
//                rows,
//                query.count());
//    }
//
//    @WsDocNote("41、强制终止任务")
//    @RequestMapping(value = "/process/kill")
//    @ResponseBody
//    public void processInstanceKill(
//            @WsDocNote("任务Id")@RequestParam("instanceId") String instanceId){
//
//        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
//                .processInstanceId(instanceId)
//                .singleResult();
//
//        if(StringUtils.isNotBlank(processInstance.getBusinessKey())){
//            docService.onBpmKilled(processInstance.getBusinessKey());
//        }
//
//        processEngine.getRuntimeService().deleteProcessInstance(instanceId,"强制结束流程");
//    }
//
//    @WsDocNote("51、拉取历史流程实例")
//    @RequestMapping(value = "/historic/process/instances")
//    @ResponseBody
//    public PageList<HistoricProcessInstance> historicProcessInstances(
//            @WsDocNote("起始条目")@RequestParam("from") Integer from,
//            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){
//
//        HistoricProcessInstanceQuery query = processEngine.getHistoryService()
//                .createHistoricProcessInstanceQuery();
//
//        return new PageList<>(
//                query.orderByProcessInstanceStartTime().desc()
//                        .listPage(from,rows),
//                from,
//                rows,
//                query.count());
//    }
//    @WsDocNote("52、拉取历史任务实例")
//    @RequestMapping(value = "/historic/tasks")
//    public List<HistoricTaskInstance> historicTasks(
//            @WsDocNote("起始条目")@RequestParam("from") Integer from,
//            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){
//        return processEngine.getHistoryService()
//                .createHistoricTaskInstanceQuery()
//                .orderByHistoricTaskInstanceStartTime().desc()
//                .listPage(from,rows);
//    }
}
