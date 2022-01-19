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

import cn.nkpro.elcube.platform.gen.UserAccount;
import cn.nkpro.elcube.security.SecurityUtilz;
import cn.nkpro.elcube.security.UserAccountService;
import cn.nkpro.elcube.task.NkBpmTaskService;
import cn.nkpro.elcube.task.model.*;
import cn.nkpro.elcube.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.impl.pvm.PvmActivity;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.DelegationState;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NkBpmTaskServiceImpl extends AbstractNkBpmSupport implements NkBpmTaskService {

    @Autowired
    private UserAccountService accountService;

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
        Assert.notNull(bpmTask.getTransition(), "任务流转不能为空");

        Task task = getTask(bpmTask.getTaskId());

        if(StringUtils.isBlank(task.getAssignee()))
            processEngine.getTaskService().claim(bpmTask.getTaskId(),SecurityUtilz.getUser().getId());

        String comment = bpmTask.getTransition().getName() + (StringUtils.isNotBlank(bpmTask.getComment())?(" | "+ bpmTask.getComment()):"");
        processEngine.getIdentityService().setAuthenticatedUserId(SecurityUtilz.getUser().getId());
        processEngine.getTaskService().createComment(bpmTask.getTaskId(),task.getProcessInstanceId(),comment);
        if(task.getDelegationState() == DelegationState.PENDING){
            processEngine.getTaskService().resolveTask(bpmTask.getTaskId(), Collections.singletonMap("NK$TRANSITION_ID",bpmTask.getTransition().getId()));
            reindex(task, task.getOwner());
        }else{
            processEngine.getTaskService().complete(bpmTask.getTaskId(), Collections.singletonMap("NK$TRANSITION_ID",bpmTask.getTransition().getId()));
        }
    }

    @Override
    @Transactional
    public void forward(BpmTaskForward bpmTask) {
        Assert.notNull(bpmTask.getAccountId(), "转办人员ID不能为空");

        Task task = getTask(bpmTask.getTaskId());

        if(StringUtils.isBlank(task.getAssignee()))
            processEngine.getTaskService().claim(bpmTask.getTaskId(),SecurityUtilz.getUser().getId());


        String comment = "转办" + (StringUtils.isNotBlank(bpmTask.getComment())?(" | "+ bpmTask.getComment()):"");
        processEngine.getIdentityService().setAuthenticatedUserId(SecurityUtilz.getUser().getId());
        processEngine.getTaskService().createComment(bpmTask.getTaskId(),task.getProcessInstanceId(),comment);
        processEngine.getTaskService().setAssignee(bpmTask.getTaskId(),bpmTask.getAccountId());

        reindex(task, bpmTask.getAccountId());
    }

    @Override
    @Transactional
    public void delegate(BpmTaskForward bpmTask) {
        Assert.notNull(bpmTask.getAccountId(), "转办人员ID不能为空");

        Task task = getTask(bpmTask.getTaskId());

        if(StringUtils.isBlank(task.getAssignee()))
            processEngine.getTaskService().claim(bpmTask.getTaskId(),SecurityUtilz.getUser().getId());

        String comment =  "委派" + (StringUtils.isNotBlank(bpmTask.getComment())?(" | "+ bpmTask.getComment()):"");
        processEngine.getIdentityService().setAuthenticatedUserId(SecurityUtilz.getUser().getId());
        processEngine.getTaskService().createComment(bpmTask.getTaskId(),task.getProcessInstanceId(),comment);
        processEngine.getTaskService().delegateTask(bpmTask.getTaskId(), bpmTask.getAccountId());

        reindex(task, bpmTask.getAccountId());
    }

    private Task getTask(String taskId){
        Assert.notNull(taskId,     "任务ID不能为空");
        Task task = processEngine.getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .or()
                .taskCandidateUser(SecurityUtilz.getUser().getId())
                .taskAssignee(SecurityUtilz.getUser().getId())
                .endOr()
                .singleResult();

        Assert.notNull(task,     "任务不存在");
        return task;
    }

    private void reindex(Task task, String assignee){
        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();

        // 因为转办不会走全局事件更新索引，所以这里需要手动更新任务索引
        BpmTaskES bpmTaskES = BpmTaskES.from(docEngine.detail(processInstance.getBusinessKey()),
                task.getId(),
                task.getName(),
                Collections.singletonList(assignee),
                "create",// 任务被强制删除
                task.getCreateTime().getTime()/1000,
                null
        );

        searchEngine.indexBeforeCommit(bpmTaskES);
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


            // 设置实例的备注
            List<Comment> processInstanceComments = processEngine.getTaskService()
                    .getProcessInstanceComments(task.getProcessInstanceId());

            Map<String, String> accounts = processInstanceComments.stream().map(Comment::getUserId).filter(Objects::nonNull).distinct()
                    .map(accountService::getAccountById)
                    .collect(Collectors.toMap(UserAccount::getId, UserAccount::getRealname));

            bpmTask.setInstanceComments(
                    processInstanceComments
                    .stream()
                    .map(comment -> {
                        BpmComment bpmComment = new BpmComment();
                        bpmComment.setComment(comment.getFullMessage());
                        bpmComment.setId(comment.getId());
                        bpmComment.setTime(comment.getTime().getTime()/1000);
                        bpmComment.setUserId(comment.getUserId());
                        bpmComment.setUser(accounts.getOrDefault(comment.getUserId(),comment.getUserId()));
                        return bpmComment;
                    })
                    .sorted(Comparator.comparing(BpmComment::getTime))
                    .collect(Collectors.toList()));

            return bpmTask;
        }

        return null;
    }

    @Override
    public boolean isDocAssignee(String businessKey, String assignee) {
        return processEngine.getTaskService()
                .createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .active()
                .or()
                    .taskCandidateUser(assignee)
                    .taskAssignee(assignee)
                .endOr().count()>0;
    }
}
