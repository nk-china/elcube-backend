package cn.nkpro.ts5.task.controller;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.data.elasticearch.ESPageList;
import cn.nkpro.ts5.data.elasticearch.SearchEngine;
import cn.nkpro.ts5.docengine.NkDocSearchService;
import cn.nkpro.ts5.task.model.BpmTaskES;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by bean on 2020/7/21.
 */
@NkNote("7.工作流")
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private NkDocSearchService searchService;
    @Autowired
    private SearchEngine searchEngine;

    @NkNote("1、拉取交易列表数据")
    @RequestMapping(value = "/tasks",method = RequestMethod.POST)
    public ESPageList<JSONObject> list(@RequestBody JSONObject params) {

        params.put("orderField",StringUtils.defaultIfBlank(params.getString("orderField"),"updatedTime"));
        return searchService.queryList(
                searchEngine.parseDocument(BpmTaskES.class),
                null,//QueryBuilders.termQuery("assignee",SecurityUtilz.getUser().getId())
                params
        );
    }

//    @WsDocNote("2、检查任务是否结束")
//    @ResponseBody
//    @RequestMapping(value = "/task/exists")
//    public Boolean taskExists(@WsDocNote("任务Id")@RequestParam("taskId") String taskId){
//        return taskService.taskExists(taskId);
//    }
//
//    @WsDocNote("3、拉取任务实例详情")
//    @RequestMapping(value = "/task")
//    public BpmTask task(@WsDocNote("任务Id")@RequestParam("taskId") String taskId){
//        return taskService.task(taskId);
//    }
//
//    @WsDocNote("4、提交任务")
//    @RequestMapping(value = "/task/complete")
//    public void complete(@RequestBody BpmTaskComplete bpmTask){
//        taskService.complete(bpmTask);
//    }
//
//    @WsDocNote("5、撤回任务")
//    @RequestMapping(value = "/task/revoke")
//    public void revoke(@WsDocNote("任务Id")@RequestParam("instanceId") String instanceId){
//        taskService.revoke(instanceId);
//    }
//
//    @WsDocNote("23、拉取实例定义模型")
//    @RequestMapping(value = "/task/definition/bpmn")
//    public JSONObject processDefinitionBpmn(
//            @RequestParam("definitionId") String definitionId) throws IOException {
//
//        ProcessDefinition definition = processEngine.getRepositoryService()
//                .createProcessDefinitionQuery()
//                .processDefinitionId(definitionId)
//                .singleResult();
//
//        JSONObject json = new JSONObject();
//        json.put("definitionId",definition.getId());
//        json.put("key",definition.getKey());
//        json.put("version",definition.getVersion());
//        json.put("bpmnXml",defBpmService.getBpmnXml(definition.getDeploymentId(),definition.getResourceName()));
//
//        return json;
//    }
}
