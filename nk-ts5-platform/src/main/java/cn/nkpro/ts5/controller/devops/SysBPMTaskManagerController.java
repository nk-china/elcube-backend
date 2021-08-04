package cn.nkpro.ts5.controller.devops;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.bpm.NkBpmTaskService;
import cn.nkpro.ts5.engine.bpm.model.BpmInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
            @WsDocNote("任务Id")@RequestParam("instanceId") String instanceId) throws Exception {
        return bpmTaskService.processInstanceDetail(instanceId);
    }

    @WsDocNote("3.强制终止流程实例")
    @RequestMapping(value = "/instance/kill")
    @ResponseBody
    public void processInstanceKill(
            @WsDocNote("任务Id")@RequestParam("instanceId") String instanceId) throws Exception {
        bpmTaskService.deleteProcessInstance(instanceId,"强制删除");
    }
}
