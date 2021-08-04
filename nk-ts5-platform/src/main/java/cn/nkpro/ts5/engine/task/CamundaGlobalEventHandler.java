package cn.nkpro.ts5.engine.task;

import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.engine.elasticearch.SearchEngine;
import cn.nkpro.ts5.engine.elasticearch.model.BpmTaskES;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
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

@SuppressWarnings("all")
@Component
public class CamundaGlobalEventHandler implements HistoryEventHandler {

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
                return;
            }

            if(HistoryEventTypes.TASK_INSTANCE_COMPLETE.getEventName().equals(historyEvent.getEventType())){
                if(StringUtils.isBlank(((HistoricTaskInstanceEventEntity) historyEvent).getDeleteReason())){
                    onTaskComplete((HistoricTaskInstanceEventEntity) historyEvent);
                }else{
                    onTaskDelete((HistoricTaskInstanceEventEntity) historyEvent);
                }
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


        Task task = getTask(event);

        DocHV doc = docEngine.detail(Context.getBpmnExecutionContext().getProcessInstance().getBusinessKey());

        BpmTaskES bpmTaskES = BeanUtilz.copyFromObject(doc, BpmTaskES.class);

        bpmTaskES.setTaskId(event.getTaskId());
        bpmTaskES.setTaskAssignee(event.getAssignee());
        bpmTaskES.setTaskName(event.getName());
        bpmTaskES.setTaskState("complete");
        bpmTaskES.setTaskStartTime(task.getCreateTime().getTime()/1000);
        bpmTaskES.setTaskEndTime(event.getEndTime().getTime()/1000);

        searchEngine.indexBeforeCommit(bpmTaskES);
    }

    // 任务被强制删除
    private void onTaskDelete(HistoricTaskInstanceEventEntity event){

        Task task = getTask(event);

        String businessKey = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(event.getProcessInstanceId())
                .singleResult()
                .getBusinessKey();

        DocHV doc = docEngine.detail(businessKey);

        BpmTaskES bpmTaskES = BeanUtilz.copyFromObject(doc, BpmTaskES.class);

        bpmTaskES.setTaskId(event.getTaskId());
        bpmTaskES.setTaskAssignee(event.getAssignee());
        bpmTaskES.setTaskName(event.getName());
        bpmTaskES.setTaskState("cancel");
        bpmTaskES.setTaskStartTime(task.getCreateTime().getTime()/1000);
        bpmTaskES.setTaskEndTime(event.getEndTime().getTime()/1000);

        searchEngine.indexBeforeCommit(bpmTaskES);
    }

    private Task getTask(HistoricTaskInstanceEventEntity event){
        return processEngine.getTaskService().createTaskQuery()
                .taskId(event.getTaskId())
                .singleResult();
    }
}
