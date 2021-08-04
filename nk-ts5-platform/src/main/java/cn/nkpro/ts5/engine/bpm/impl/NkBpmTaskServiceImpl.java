package cn.nkpro.ts5.engine.bpm.impl;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.config.security.SecurityUtilz;
import cn.nkpro.ts5.engine.bpm.NkBpmTaskService;
import cn.nkpro.ts5.engine.bpm.model.BpmInstance;
import cn.nkpro.ts5.engine.bpm.model.BpmTask;
import cn.nkpro.ts5.engine.bpm.model.BpmTaskComplete;
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NkBpmTaskServiceImpl implements NkBpmTaskService {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private NkDocEngineFrontService docEngine;

    @Override
    @Transactional
    public ProcessInstance start(String key, String docId){

        Map<String,Object> variables = new HashMap<>();
        variables.put("NK@START_USER_ID", SecurityUtilz.getUser().getId());

        return processEngine.getRuntimeService()
                .startProcessInstanceByKey(
                        key,
                        docId,
                        variables
                );
    }

    @Override
    @Transactional
    public void complete(BpmTaskComplete bpmTask){
        Assert.notNull(bpmTask.getTaskId(),"任务ID不能为空");

        Task task = processEngine.getTaskService()
                .createTaskQuery()
                .taskId(bpmTask.getTaskId())
                .singleResult();
        Assert.notNull(task,"任务不存在");

        String comment = bpmTask.getFlowName() + (StringUtils.isNotBlank(bpmTask.getComment())?(" | "+ bpmTask.getComment()):"");
        processEngine.getTaskService().createComment(bpmTask.getTaskId(),task.getProcessInstanceId(),comment);

        bpmTask.getVariables().put("nkFlowId",bpmTask.getFlow());
        processEngine.getTaskService().complete(bpmTask.getTaskId(),bpmTask.getVariables());
    }

    @Override
    public PageList<BpmInstance> processInstancePage(Integer from,Integer rows){

        HistoricProcessInstanceQuery query = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime()
                .desc();

        return new PageList<>(
                BeanUtilz.copyFromList(query.listPage(from, rows),BpmInstance.class),
                from,
                rows,
                query.count());
    }

    @Override
    public BpmInstance processInstanceDetail(String instanceId){

        BpmInstance processInstance = BeanUtilz.copyFromObject(
                processEngine.getHistoryService()
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(instanceId)
                    .singleResult(),BpmInstance.class);

        Assert.notNull(processInstance, "流程实例不存在");

        processInstance.setBpmTask(BeanUtilz.copyFromList(
                processEngine.getHistoryService()
                        .createHistoricTaskInstanceQuery()
                        .processInstanceId(instanceId)
                        .list(),BpmTask.class));

        if(StringUtils.equals(processInstance.getState(),"ACTIVE")){
            processInstance.setBpmVariables(processEngine.getRuntimeService()
                    .getVariables(instanceId));
        }

        return processInstance;
    }









    @Override
    public Boolean taskExists(String taskId) {
        return processEngine.getTaskService()
                .createTaskQuery()
                .taskId(taskId).count()>0;
    }

    @Override
    public BpmTask task(String taskId){

        Task task = processEngine.getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();

        Assert.notNull(task,"任务不存在");

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();

        BpmTask bpmTask = new BpmTask();

//        if(StringUtils.isNotBlank(processInstance.getBusinessKey())){
//            BizDoc bizDoc = docEngineWithPerm.getDetailHasDocPermForController(processInstance.getBusinessKey());
//            BeanUtilz.copyFromObject(bizDoc,      bpmTask);
//        }
//
//        BeanUtilz.copyFromObject(processInstance, bpmTask);
//        BeanUtilz.copyFromObject(task,            bpmTask);
//
//
//
//        FlowElement flowElement = processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId())
//                .getFlowElement(task.getTaskDefinitionKey());
//
//        List<String> button = NkBPMUtils.getProperty(flowElement, "button");
//
//        if(button!=null){
//            bpmTask.setFlows(
//                    button
//                            .stream()
//                            .map(btn->{
//                                BpmTaskFlow flow = new BpmTaskFlow();
//                                flow.setId(btn);
//                                return flow;
//                            })
//                            .collect(Collectors.toList())
//            );
//        }else{
//            bpmTask.setFlows(
//                    ((FlowNode) flowElement).getOutgoingFlows()
//                            .stream()
//                            .map(sequenceFlow -> {
//                                BpmTaskFlow flow = new BpmTaskFlow();
//                                BeanUtilz.copyFromObject(sequenceFlow,flow);
//                                return flow;
//                            })
//                            .collect(Collectors.toList())
//            );
//        }

        return bpmTask;
    }

    @Override
    public List<BpmInstance> getProcessInfoByDocId(String docId){

        return processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(docId)
                .list()
                .stream()
                .map(instance -> {
                    BpmInstance bpmInstance = BeanUtilz.copyFromObject(instance, BpmInstance.class);
//                    SysAccount u = accountService.getAccountById(bpmInstance.getStartUserId());
//
//                    if(StringUtils.isBlank(bpmInstance.getName())){
//                        bpmInstance.setName(instance.getProcessDefinitionName());
//                    }
//                    bpmInstance.setStartUser(u!=null?u.getRealname():null);
//                    if(instance.getStartTime()!=null){
//                        bpmInstance.setStartDate(instance.getStartTime().getTime()/1000);
//                    }
//
//                    List<Comment> comments = processEngine.getTaskService()
//                            .getProcessInstanceComments(instance.getId());
//
////                    List<HistoricTaskInstance> list = processEngine.getHistoryService()
////                            .createHistoricTaskInstanceQuery()
////                            .processInstanceId(instance.getId())
////                            .list();
//
//                    List<BpmActivity> activities = processEngine.getHistoryService()
//                            .createHistoricActivityInstanceQuery()
//                            .processInstanceId(instance.getId())
//                            .orderByHistoricActivityInstanceStartTime().asc()
//                            .activityTypes(logActivityTypes)
//                            .list()
//                            .stream()
//                            .map(activity -> {
//                                BpmActivity bpmActivity = BeanUtilz.copyFromObject(activity, BpmActivity.class);
//                                if (activity.getStartTime() != null) {
//                                    bpmActivity.setStartDate(activity.getStartTime().getTime() / 1000);
//                                }
//                                if (activity.getEndTime() != null) {
//                                    bpmActivity.setEndDate(activity.getEndTime().getTime() / 1000);
//                                }
//                                if(StringUtils.equals(activity.getActivityType(),"userTask")){
//
//                                    if(StringUtils.isNotBlank(activity.getAssignee())){
//                                        SysAccount user = accountService.getAccountById(activity.getAssignee());
//                                        bpmActivity.setUser(user != null ? user.getRealname() : null);
//                                    }else if(activity.getEndTime() == null){
//                                        List<IdentityLink> links = processEngine.getTaskService().getIdentityLinksForTask(activity.getTaskId());
//                                        String realnames = links.stream()
//                                                .map(link->accountService.getAccountById(link.getUserId()) == null?(new SysAccount()):accountService.getAccountById(link.getUserId()))
//                                                .map(SysAccount::getRealname)
//                                                .collect(Collectors.joining(","));
//                                        bpmActivity.setUser(realnames);
//                                    }
//                                }
//
//
//                                bpmActivity.setComments(
//                                        comments.stream()
//                                                .filter(comment -> comment.getTaskId().equals(activity.getTaskId()))
//                                                .map(comment -> {
//                                                    BpmComment bpmComment = BeanUtilz.copyFromObject(comment, BpmComment.class);
//                                                    bpmComment.setComment(comment.getFullMessage());
//                                                    bpmComment.setTime(comment.getTime().getTime() / 1000);
//
//                                                    SysAccount accountById = accountService.getAccountById(comment.getUserId());
//                                                    bpmComment.setUser(accountById != null ? accountById.getRealname() : null);
//                                                    return bpmComment;
//                                                })
//                                                .collect(Collectors.toList())
//                                );
//
//                                return bpmActivity;
//                            }).collect(Collectors.toList());
//
//                    bpmInstance.setActivities(activities);
//
//                    if(instance.getEndTime()!=null){
//                        bpmInstance.setEndDate(instance.getEndTime().getTime()/1000);
//
//                        BpmActivity activity = new BpmActivity();
//                        activity.setStartDate(bpmInstance.getEndDate());
//                        activity.setEndDate(bpmInstance.getEndDate());
//                        activity.setActivityName("结束");
//                        if(StringUtils.isNotBlank(bpmInstance.getDeleteReason())){
//                            BpmComment bpmComment = new BpmComment();
//                            bpmComment.setComment(bpmInstance.getDeleteReason());
//                            activity.setComments(Collections.singletonList(bpmComment));
//                        }
//
//                        activities.add(activity);
//                    }else{
//                        if(StringUtils.equals(instance.getStartUserId(),SecurityUtilz.getUser().getId())){
//                            Map<String, Object> vars = processEngine.getRuntimeService().getVariables(instance.getId());
//                            bpmInstance.setRevokeAble((Boolean) vars.get("$REVOKE_ABLE"));
//                        }
//                    }
//
                    return bpmInstance;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void reIndexTask(){

        List<HistoricProcessInstance> instances = processEngine.getHistoryService().createHistoricProcessInstanceQuery().list();

        processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .list()
                .forEach((task) -> {

//                    String businessKey = instances.stream()
//                            .filter(instance -> StringUtils.equals(instance.getId(), task.getProcessInstanceId()))
//                            .findFirst()
//                            .map(HistoricProcessInstance::getBusinessKey)
//                            .orElse(null);
//
//                    if(businessKey!=null){
//
//                        List<String> collect = null;
//                        if(StringUtils.isBlank(task.getAssignee())){
//                            collect = processEngine.getHistoryService()
//                                    .getHistoricIdentityLinksForTask(task.getId())
//                                    .stream()
//                                    .map(historicIdentityLink -> historicIdentityLink.getUserId())
//                                    .collect(Collectors.toList());
//                        }
//
//                        taskIndexManager.index(
//                                task,
//                                collect,
//                                task.getEndTime() != null,
//                                businessKey
//                        );
//                    }
                });
    }

    @Override
    public void revoke(String instanceId) {
        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();

//        System.out.println(processInstance.getProcessVariables());
//
//        if(StringUtils.isNotBlank(processInstance.getBusinessKey())){
//            docService.onBpmKilled(processInstance.getBusinessKey());
//        }

        processEngine.getRuntimeService().deleteProcessInstance(instanceId,"撤回流程");
    }

//    private AuthenticationContext authenticationContext = new AuthenticationContext(){
//
//        @Override
//        public String getAuthenticatedUserId() {
//            return SecurityUtilz.getUser().getId();
//        }
//
//        @Override
//        public Principal getPrincipal() {
//            return () -> SecurityUtilz.getUser().getRealname();
//        }
//
//        @Override
//        public void setPrincipal(Principal principal) {}
//    };

    @Override
    @Transactional
    public void deleteProcessInstance(String instanceId, String deleteReason){
        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();

        if(StringUtils.isNotBlank(processInstance.getBusinessKey())){
            docEngine.onBpmKilled(
                    processInstance.getBusinessKey(),
                    processInstance.getProcessDefinitionId().split(":")[0],
                    "强制结束流程");
        }

        processEngine.getRuntimeService().setVariable(instanceId,"NK@DELETE",true);
        processEngine.getRuntimeService().deleteProcessInstance(instanceId,"强制结束流程");
    }
}
