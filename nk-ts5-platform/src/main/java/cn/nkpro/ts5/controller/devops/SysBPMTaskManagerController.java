package cn.nkpro.ts5.controller.devops;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.bpm.NkBpmTaskService;
import cn.nkpro.ts5.engine.bpm.model.BpmInstance;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by bean on 2020/7/17.
 */
@WsDocNote("S1.工作流配置")
@RestController
@RequestMapping("/ops/bpm")
@PreAuthorize("hasAnyAuthority('*:*','DEF:*','DEF:BPM')")
public class SysBPMTaskManagerController {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private NkBpmTaskService bpmTaskService;
    @Autowired
    private NkDocEngineFrontService docEngine;

    @WsDocNote("31.拉取流程实例")
    @RequestMapping(value = "/instances")
    public PageList<BpmInstance> processInstances(
            @WsDocNote("起始条目")@RequestParam("from") Integer from,
            @WsDocNote("查询条目")@RequestParam("rows") Integer rows){

        HistoricProcessInstanceQuery query = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .active().orderByProcessInstanceStartTime()
                .desc();

        return new PageList<>(
                BeanUtilz.copyFromList(query.listPage(from, rows),BpmInstance.class),
                from,
                rows,
                query.count());
    }

    @WsDocNote("41、强制终止任务")
    @RequestMapping(value = "/instance/kill")
    @ResponseBody
    public void processInstanceKill(
            @WsDocNote("任务Id")@RequestParam("instanceId") String instanceId) throws Exception {
        bpmTaskService.deleteProcessInstance(instanceId,"强制删除");
    }
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
