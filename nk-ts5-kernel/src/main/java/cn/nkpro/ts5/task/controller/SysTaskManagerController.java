package cn.nkpro.ts5.task.controller;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.task.model.BpmTaskComplete;
import cn.nkpro.ts5.task.NkBpmTaskService;
import cn.nkpro.ts5.task.NkBpmTaskManager;
import cn.nkpro.ts5.task.model.BpmInstance;
import cn.nkpro.ts5.annotation.NkNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bean on 2020/7/17.
 */
@NkNote("21.[DevOps]工作流管理")
@RestController
@RequestMapping("/ops/bpm")
@PreAuthorize("hasAnyAuthority('*:*','DEVOPS:*','DEVOPS:BPM')")
public class SysTaskManagerController {

    @Autowired@SuppressWarnings("all")
    private NkBpmTaskManager bpmTaskManager;
    @Autowired@SuppressWarnings("all")
    private NkBpmTaskService bpmTaskService;

    @NkNote("1.拉取流程实例")
    @RequestMapping(value = "/instances")
    public PageList<BpmInstance> processInstances(
            @NkNote("起始条目")@RequestParam("from") Integer from,
            @NkNote("查询条目")@RequestParam("rows") Integer rows){
        return bpmTaskManager.processInstancePage(from, rows);
    }

    @NkNote("2.拉取流程详情")
    @RequestMapping(value = "/instance/detail")
    @ResponseBody
    public BpmInstance processInstanceDetail(
            @NkNote("任务Id")@RequestParam("instanceId") String instanceId) {
        return bpmTaskManager.processInstanceDetail(instanceId);
    }

    @NkNote("3.强制执行任务")
    @RequestMapping(value = "/instance/complete")
    @ResponseBody
    public void processCompleteTask(
            @NkNote("任务Id")@RequestBody BpmTaskComplete taskComplete) {
        bpmTaskService.complete(taskComplete);
    }

    @NkNote("4.强制终止流程实例")
    @RequestMapping(value = "/instance/kill")
    @ResponseBody
    public void processInstanceKill(
            @NkNote("任务Id")@RequestParam("instanceId") String instanceId,
            @NkNote("删除原因")@RequestParam("deleteReason") String deleteReason) {
        bpmTaskManager.deleteProcessInstance(instanceId,deleteReason);
    }
}
