package cn.nkpro.ts5.engine.task.impl;

import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.BpmTaskES;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
@Component
public class NkGlobalEventHandler implements HistoryEventHandler {

    @Autowired@Lazy
    private NkDocEngineFrontService docEngine;
    @Autowired@Lazy
    private SearchEngine searchEngine;
    @Autowired@Lazy
    private ProcessEngine processEngine;

    @Override
    public void handleEvents(List<HistoryEvent> list) {
        list.forEach(this::handleEvent);
    }

    @Override
    public void handleEvent(HistoryEvent historyEvent) {

        if(historyEvent instanceof HistoricTaskInstanceEventEntity){

            if(HistoryEventTypes.TASK_INSTANCE_CREATE.getEventName().equals(historyEvent.getEventType())){
                onTaskCreate((HistoricTaskInstanceEventEntity) historyEvent);
            }else
            if(HistoryEventTypes.TASK_INSTANCE_COMPLETE.getEventName().equals(historyEvent.getEventType())){
                onTaskComplete((HistoricTaskInstanceEventEntity) historyEvent);
            }
        }
    }

    // 任务被创建
    private void onTaskCreate(HistoricTaskInstanceEventEntity event){

        DocHV doc = docEngine.detail(Context.getBpmnExecutionContext().getProcessInstance().getBusinessKey());

        BpmTaskES bpmTaskES = BeanUtilz.copyFromObject(doc, BpmTaskES.class);

        bpmTaskES.setTaskId(event.getTaskId());
        bpmTaskES.setTaskAssignee(event.getAssignee());
        bpmTaskES.setTaskName(event.getName());
        bpmTaskES.setTaskState("create");
        bpmTaskES.setTaskStartTime(event.getStartTime().getTime()/1000);

        searchEngine.indexBeforeCommit(bpmTaskES);
    }

    // 任务被提交
    private void onTaskComplete(HistoricTaskInstanceEventEntity event){

        Map<String, Object> variables = processEngine.getRuntimeService().getVariables(event.getProcessInstanceId());
        Task task = processEngine.getTaskService().createTaskQuery().taskId(event.getTaskId()).singleResult();
        DocHV doc = docEngine.detail((String) variables.get("NK$BUSINESS_KEY"));

        BpmTaskES bpmTaskES = BeanUtilz.copyFromObject(doc, BpmTaskES.class);

        bpmTaskES.setTaskId(event.getTaskId());
        bpmTaskES.setTaskAssignee(event.getAssignee());
        bpmTaskES.setTaskName(event.getName());
        bpmTaskES.setTaskState(variables.containsKey("NK$DELETE")?"delete":"complete"); // 任务被强制删除
        bpmTaskES.setTaskStartTime(task.getCreateTime().getTime()/1000);
        bpmTaskES.setTaskEndTime(event.getEndTime().getTime()/1000);

        searchEngine.indexBeforeCommit(bpmTaskES);
    }
}
