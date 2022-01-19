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

import cn.nkpro.elcube.docengine.NkDocEngineThreadLocal;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.task.model.BpmTaskES;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.history.event.HistoricIdentityLinkLogEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

            if(HistoryEventTypes.TASK_INSTANCE_UPDATE.getEventName().equals(historyEvent.getEventType())){
                //onTaskUpdate((HistoricTaskInstanceEventEntity) historyEvent);
            }else
            if(HistoryEventTypes.TASK_INSTANCE_CREATE.getEventName().equals(historyEvent.getEventType())){
                onTaskCreate((HistoricTaskInstanceEventEntity) historyEvent);
            }else
            if(HistoryEventTypes.TASK_INSTANCE_COMPLETE.getEventName().equals(historyEvent.getEventType())){
                onTaskComplete((HistoricTaskInstanceEventEntity) historyEvent);
            }
        }else if(historyEvent instanceof HistoricIdentityLinkLogEventEntity){
            if(HistoryEventTypes.IDENTITY_LINK_ADD.getEventName().equals(historyEvent.getEventType())){
                //onTaskCreate((HistoricIdentityLinkLogEventEntity) historyEvent);
            }
        }
    }

    private void onTaskUpdate(HistoricTaskInstanceEventEntity event){

        DocHV doc = NkDocEngineThreadLocal.getCurr();
        if(doc==null){
            String businessKey = Context.getBpmnExecutionContext()!=null
                    ? Context.getBpmnExecutionContext().getProcessInstance().getBusinessKey()
                    : null;
            if(StringUtils.isBlank(businessKey)){
                Map<String, Object> variables = processEngine.getRuntimeService().getVariables(event.getProcessInstanceId());
                businessKey = (String) variables.get("NK$BUSINESS_KEY");
            }
            Assert.notNull(businessKey,"工作流关联单据没有找到");
            doc = docEngine.detail(businessKey);
        }
        Task task = processEngine.getTaskService().createTaskQuery().taskId(event.getTaskId()).singleResult();

        Long startTime = task.getCreateTime().getTime()/1000;

        BpmTaskES bpmTaskES = BpmTaskES.from(doc,
                event.getId(),
                event.getName(),
                assignee(event.getId(),event.getAssignee()),
                "create",
                startTime,
                null
        );

        searchEngine.indexBeforeCommit(bpmTaskES);
    }

    // 任务被指派
    private void onTaskCreate(HistoricTaskInstanceEventEntity event){

        DocHV doc = NkDocEngineThreadLocal.getCurr();
        if(doc==null){
            doc = docEngine.detail(Context.getBpmnExecutionContext().getProcessInstance().getBusinessKey());
        }

        List<String> identityLinks = processEngine.getTaskService().getIdentityLinksForTask(event.getId())
                .stream()
                .filter(identityLink -> StringUtils.equals(identityLink.getType(),"candidate"))
                .map(IdentityLink::getUserId)
                .collect(Collectors.toList());

        if(StringUtils.isNotBlank(event.getAssignee()))
            identityLinks.add(event.getAssignee());


        BpmTaskES bpmTaskES = BpmTaskES.from(doc,
                event.getId(),
                event.getName(),
                assignee(event.getId(),event.getAssignee()),
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

        long startTime = event.getStartTime()!=null?event.getStartTime().getTime()/1000:task.getCreateTime().getTime()/1000;
        long endTime   = event.getEndTime()!=null?event.getEndTime().getTime()/1000:System.currentTimeMillis()/1000;

        BpmTaskES bpmTaskES = BpmTaskES.from(doc,
                event.getId(),
                event.getName(),
                assignee(event.getId(),event.getAssignee()),
                variables.containsKey("NK$DELETE")?"delete":"complete",// 任务被强制删除
                startTime,
                endTime
        );

        searchEngine.indexBeforeCommit(bpmTaskES);
    }
}
