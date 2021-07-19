package cn.nkpro.tfms.platform.controllers;

import cn.nkpro.tfms.platform.elasticearch.ESPageList;
import cn.nkpro.tfms.platform.model.bpm.BpmTask;
import cn.nkpro.tfms.platform.model.bpm.BpmTaskComplete;
import cn.nkpro.tfms.platform.model.index.IndexDocItem;
import cn.nkpro.ts5.config.mvc.CompressResponse;
import cn.nkpro.tfms.platform.services.TfmsDefBpmService;
import cn.nkpro.tfms.platform.services.TfmsIndexService;
import cn.nkpro.tfms.platform.services.TfmsTaskService;
import cn.nkpro.ts5.utils.SecurityUtilz;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import org.elasticsearch.index.query.QueryBuilders;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


/**
 * Created by bean on 2020/7/21.
 */
@WsDocNote("14.工作流")
@Controller
@RequestMapping("/bpm")
public class BPMController {

    @Autowired
    private TfmsIndexService indexService;
    @Autowired
    private TfmsTaskService taskService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private TfmsDefBpmService defBpmService;

    @WsDocNote("1、拉取交易列表数据")
    @CompressResponse
    @RequestMapping(value = "/tasks",method = RequestMethod.POST)
    public ESPageList<IndexDocItem> list(@RequestBody JSONObject params) {

        params.put("orderField",StringUtils.defaultIfBlank(params.getString("orderField"),"itemState"));
        return indexService.queryList(
                IndexDocItem.class,
                QueryBuilders.termQuery("dynamics.assignee_keyword",
                        SecurityUtilz.getUser().getId()),params
        );
    }

    @WsDocNote("2、检查任务是否结束")
    @ResponseBody
    @RequestMapping(value = "/task/exists")
    public Boolean taskExists(@WsDocNote("任务Id")@RequestParam("taskId") String taskId){
        return taskService.taskExists(taskId);
    }

    @WsDocNote("3、拉取任务实例详情")
    @CompressResponse
    @RequestMapping(value = "/task")
    public BpmTask task(@WsDocNote("任务Id")@RequestParam("taskId") String taskId){
        return taskService.task(taskId);
    }

    @WsDocNote("4、提交任务")
    @ResponseBody
    @RequestMapping(value = "/task/complete")
    public void complete(@RequestBody BpmTaskComplete bpmTask){
        taskService.complete(bpmTask);
    }

    @WsDocNote("5、撤回任务")
    @ResponseBody
    @RequestMapping(value = "/task/revoke")
    public void revoke(@WsDocNote("任务Id")@RequestParam("instanceId") String instanceId){
        taskService.revoke(instanceId);
    }

    @WsDocNote("23、拉取实例定义模型")
    @RequestMapping(value = "/task/definition/bpmn")
    @ResponseBody
    public JSONObject processDefinitionBpmn(
            @RequestParam("definitionId") String definitionId) throws IOException {

        ProcessDefinition definition = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();

        JSONObject json = new JSONObject();
        json.put("definitionId",definition.getId());
        json.put("key",definition.getKey());
        json.put("version",definition.getVersion());
        json.put("bpmnXml",defBpmService.getBpmnXml(definition.getDeploymentId(),definition.getResourceName()));

        return json;
    }
}
