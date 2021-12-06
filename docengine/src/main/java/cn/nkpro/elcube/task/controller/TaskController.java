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
package cn.nkpro.elcube.task.controller;

import cn.nkpro.elcube.annotation.NkNote;
import cn.nkpro.elcube.data.elasticearch.ESPageList;
import cn.nkpro.elcube.data.elasticearch.SearchEngine;
import cn.nkpro.elcube.docengine.NkDocSearchService;
import cn.nkpro.elcube.docengine.model.SearchParams;
import cn.nkpro.elcube.task.model.BpmTaskES;
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
    public ESPageList<JSONObject> list(@RequestBody SearchParams params) {

        params.setOrderField(StringUtils.defaultIfBlank(params.getOrderField(),"updatedTime"));
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
