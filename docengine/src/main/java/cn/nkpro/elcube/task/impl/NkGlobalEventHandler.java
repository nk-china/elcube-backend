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
package cn.nkpro.elcube.task.impl;

import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.task.model.BpmTaskES;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
@Component
public class NkGlobalEventHandler extends AbstractNkBpmSupport implements HistoryEventHandler {

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

        BpmTaskES bpmTaskES = BpmTaskES.from(doc,
                event.getId(),
                event.getName(),
                event.getAssignee(),
                "create",
                event.getStartTime().getTime()/1000,
                null
        );

        searchEngine.indexBeforeCommit(bpmTaskES);
    }

    // 任务被提交
    private void onTaskComplete(HistoricTaskInstanceEventEntity event){

        Map<String, Object> variables = processEngine.getRuntimeService().getVariables(event.getProcessInstanceId());
        Task task = processEngine.getTaskService().createTaskQuery().taskId(event.getTaskId()).singleResult();
        DocHV doc = docEngine.detail((String) variables.get("NK$BUSINESS_KEY"));

        BpmTaskES bpmTaskES = BpmTaskES.from(doc,
                event.getId(),
                event.getName(),
                event.getAssignee(),
                variables.containsKey("NK$DELETE")?"delete":"complete",// 任务被强制删除
                event.getStartTime().getTime()/1000,
                event.getEndTime().getTime()/1000
        );

        searchEngine.indexBeforeCommit(bpmTaskES);
    }
}
