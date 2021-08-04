package cn.nkpro.ts5.controller.devops;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.task.NkBpmTaskService;
import cn.nkpro.ts5.engine.task.model.BpmInstance;
import cn.nkpro.ts5.engine.task.model.BpmTaskComplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("21.[DevOps]工作流管理")
@RestController
@RequestMapping("/ops/bpm")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:BPM')")
public class SysBPMTaskManagerController {

    @Autowired@SuppressWarnings("all")
    private NkBpmTaskService bpmTaskService;

    @WsDocNote("1.拉取流程实例")
    @RequestMapping(value = "/instances")
    public PageList<BpmInstance> processInstances(
            @WsDocNote("起始条目")@RequestParam("from") Integer from,
            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){
        return bpmTaskService.processInstancePage(from, rows);
    }

    @WsDocNote("2.拉取流程详情")
    @RequestMapping(value = "/instance/detail")
    @ResponseBody
    public BpmInstance processInstanceDetail(
            @WsDocNote("任务Id")@RequestParam("instanceId") String instanceId) {
        return bpmTaskService.processInstanceDetail(instanceId);
    }

    @WsDocNote("3.强制执行任务")
    @RequestMapping(value = "/instance/complete")
    @ResponseBody
    public void processCompleteTask(
            @WsDocNote("任务Id")@RequestBody BpmTaskComplete taskComplete) {
        bpmTaskService.complete(taskComplete);
    }

    @WsDocNote("4.强制终止流程实例")
    @RequestMapping(value = "/instance/kill")
    @ResponseBody
    public void processInstanceKill(
            @WsDocNote("任务Id")@RequestParam("instanceId") String instanceId,
            @WsDocNote("删除原因")@RequestParam("deleteReason") String deleteReason) {
        bpmTaskService.deleteProcessInstance(instanceId,deleteReason);
    }
}
