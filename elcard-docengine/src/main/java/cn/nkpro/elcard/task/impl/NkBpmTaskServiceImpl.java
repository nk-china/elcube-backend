/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.task.impl;

import cn.nkpro.elcard.security.SecurityUtilz;
import cn.nkpro.elcard.task.NkBpmTaskService;
import cn.nkpro.elcard.task.model.BpmTask;
import cn.nkpro.elcard.task.model.BpmTaskComplete;
import cn.nkpro.elcard.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.impl.pvm.PvmActivity;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NkBpmTaskServiceImpl extends AbstractNkBpmSupport implements NkBpmTaskService {

    @Override
    @Transactional
    public String start(String key, String docId){

        Map<String,Object> variables = new HashMap<>();
        variables.put("NK$START_USER_ID", SecurityUtilz.getUser().getId());
        variables.put("NK$BUSINESS_KEY",docId);

        return processEngine.getRuntimeService()
                .startProcessInstanceByKey(
                        key,
                        docId,
                        variables
                ).getProcessInstanceId();
    }

    @Override
    @Transactional
    public void complete(BpmTaskComplete bpmTask){
        Assert.notNull(bpmTask.getTaskId(),     "任务ID不能为空");
        Assert.notNull(bpmTask.getTransition(), "任务流转不能为空");

        Task task = processEngine.getTaskService()
                .createTaskQuery()
                .taskId(bpmTask.getTaskId())
                .singleResult();
        Assert.notNull(task,"任务不存在");

        String comment = bpmTask.getTransition().getName() + (StringUtils.isNotBlank(bpmTask.getComment())?(" | "+ bpmTask.getComment()):"");
        processEngine.getTaskService().setAssignee(bpmTask.getTaskId(),SecurityUtilz.getUser().getId());
        processEngine.getTaskService().createComment(bpmTask.getTaskId(),task.getProcessInstanceId(),comment);
        processEngine.getTaskService().complete(bpmTask.getTaskId(), Collections.singletonMap("NK$TRANSITION_ID",bpmTask.getTransition().getId()));
    }

    @Override
    public BpmTask taskByBusinessAndAssignee(String businessKey, String assignee){

        List<Task> tasks = processEngine.getTaskService()
                .createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .active()
                .or()
                    .taskCandidateUser(assignee)
                    .taskAssignee(assignee)
                .endOr()
                .listPage(0,1);

        if(!tasks.isEmpty()){

            Task task = tasks.get(0);

            BpmTask bpmTask = BeanUtilz.copyFromObject(task, BpmTask.class);

            // 获取流程图内所有的节点
            List<? extends PvmActivity> activities = getProcessDefinitionActivities(task.getProcessDefinitionId());

            // 当前任务节点的对外连接线
            bpmTask.setTransitions(getTaskTransition(activities, task.getTaskDefinitionKey()));

            return bpmTask;
        }

        return null;
    }
}
