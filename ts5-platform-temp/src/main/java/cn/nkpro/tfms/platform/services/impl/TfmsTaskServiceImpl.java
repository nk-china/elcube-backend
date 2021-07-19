package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.bpm.NkBPMUtils;
import cn.nkpro.tfms.platform.model.bpm.*;
import cn.nkpro.tfms.platform.model.po.BizDoc;
import cn.nkpro.tfms.platform.model.po.SysAccount;
import cn.nkpro.tfms.platform.services.*;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.SecurityUtilz;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.common.engine.api.identity.AuthenticationContext;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TfmsTaskServiceImpl implements TfmsTaskService {

    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private TfmsDocService docService;
    @Autowired
    private TfmsDocEngineWithPerm docEngineWithPerm;
    @Autowired
    private TfmsTaskIndexManager taskIndexManager;
    @Autowired
    private TfmsSysAccountService accountService;

    private static Set<String> logActivityTypes = new HashSet<>();
    static {
        logActivityTypes.add("startEvent");
        logActivityTypes.add("stopEvent");
        logActivityTypes.add("userTask");
        logActivityTypes.add("serviceTask");
    }

    @Override
    @Transactional
    public void start(String key, String docId){
        Authentication.setAuthenticationContext(authenticationContext);

        // todo 流程变量待处理
        Map<String,Object> variables = new HashMap<>();
        variables.put("NK_START_USER_ID", Authentication.getAuthenticatedUserId());

        processEngine.getRuntimeService()
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

        Authentication.setAuthenticationContext(authenticationContext);

        processEngine.getTaskService().setAssignee(bpmTask.getTaskId(), Authentication.getAuthenticatedUserId());

        String comment = bpmTask.getFlowName() + (StringUtils.isNotBlank(bpmTask.getComment())?(" | "+ bpmTask.getComment()):"");
        processEngine.getTaskService().addComment(bpmTask.getTaskId(),task.getProcessInstanceId(),comment);

        bpmTask.getVariables().put("nkFlowId",bpmTask.getFlow());
        processEngine.getTaskService().complete(bpmTask.getTaskId(),bpmTask.getVariables());
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

        if(StringUtils.isNotBlank(processInstance.getBusinessKey())){
            BizDoc bizDoc = docEngineWithPerm.getDetailHasDocPermForController(processInstance.getBusinessKey());
            BeanUtilz.copyFromObject(bizDoc,      bpmTask);
        }

        BeanUtilz.copyFromObject(processInstance, bpmTask);
        BeanUtilz.copyFromObject(task,            bpmTask);



        FlowElement flowElement = processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId())
                .getFlowElement(task.getTaskDefinitionKey());

        List<String> button = NkBPMUtils.getProperty(flowElement, "button");

        if(button!=null){
            bpmTask.setFlows(
                    button
                            .stream()
                            .map(btn->{
                                BpmTaskFlow flow = new BpmTaskFlow();
                                flow.setId(btn);
                                return flow;
                            })
                            .collect(Collectors.toList())
            );
        }else{
            bpmTask.setFlows(
                    ((FlowNode) flowElement).getOutgoingFlows()
                            .stream()
                            .map(sequenceFlow -> {
                                BpmTaskFlow flow = new BpmTaskFlow();
                                BeanUtilz.copyFromObject(sequenceFlow,flow);
                                return flow;
                            })
                            .collect(Collectors.toList())
            );
        }

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
                    SysAccount u = accountService.getAccountById(bpmInstance.getStartUserId());

                    if(StringUtils.isBlank(bpmInstance.getName())){
                        bpmInstance.setName(instance.getProcessDefinitionName());
                    }
                    bpmInstance.setStartUser(u!=null?u.getRealname():null);
                    if(instance.getStartTime()!=null){
                        bpmInstance.setStartDate(instance.getStartTime().getTime()/1000);
                    }

                    List<Comment> comments = processEngine.getTaskService()
                            .getProcessInstanceComments(instance.getId());

//                    List<HistoricTaskInstance> list = processEngine.getHistoryService()
//                            .createHistoricTaskInstanceQuery()
//                            .processInstanceId(instance.getId())
//                            .list();

                    List<BpmActivity> activities = processEngine.getHistoryService()
                            .createHistoricActivityInstanceQuery()
                            .processInstanceId(instance.getId())
                            .orderByHistoricActivityInstanceStartTime().asc()
                            .activityTypes(logActivityTypes)
                            .list()
                            .stream()
                            .map(activity -> {
                                BpmActivity bpmActivity = BeanUtilz.copyFromObject(activity, BpmActivity.class);
                                if (activity.getStartTime() != null) {
                                    bpmActivity.setStartDate(activity.getStartTime().getTime() / 1000);
                                }
                                if (activity.getEndTime() != null) {
                                    bpmActivity.setEndDate(activity.getEndTime().getTime() / 1000);
                                }
                                if(StringUtils.equals(activity.getActivityType(),"userTask")){

                                    if(StringUtils.isNotBlank(activity.getAssignee())){
                                        SysAccount user = accountService.getAccountById(activity.getAssignee());
                                        bpmActivity.setUser(user != null ? user.getRealname() : null);
                                    }else if(activity.getEndTime() == null){
                                        List<IdentityLink> links = processEngine.getTaskService().getIdentityLinksForTask(activity.getTaskId());
                                        String realnames = links.stream()
                                                .map(link->accountService.getAccountById(link.getUserId()))
                                                .map(SysAccount::getRealname)
                                                .collect(Collectors.joining(","));
                                        bpmActivity.setUser(realnames);
                                    }
                                }


                                bpmActivity.setComments(
                                        comments.stream()
                                                .filter(comment -> comment.getTaskId().equals(activity.getTaskId()))
                                                .map(comment -> {
                                                    BpmComment bpmComment = BeanUtilz.copyFromObject(comment, BpmComment.class);
                                                    bpmComment.setComment(comment.getFullMessage());
                                                    bpmComment.setTime(comment.getTime().getTime() / 1000);

                                                    SysAccount accountById = accountService.getAccountById(comment.getUserId());
                                                    bpmComment.setUser(accountById != null ? accountById.getRealname() : null);
                                                    return bpmComment;
                                                })
                                                .collect(Collectors.toList())
                                );

                                return bpmActivity;
                            }).collect(Collectors.toList());

                    bpmInstance.setActivities(activities);

                    if(instance.getEndTime()!=null){
                        bpmInstance.setEndDate(instance.getEndTime().getTime()/1000);

                        BpmActivity activity = new BpmActivity();
                        activity.setStartDate(bpmInstance.getEndDate());
                        activity.setEndDate(bpmInstance.getEndDate());
                        activity.setActivityName("结束");
                        if(StringUtils.isNotBlank(bpmInstance.getDeleteReason())){
                            BpmComment bpmComment = new BpmComment();
                            bpmComment.setComment(bpmInstance.getDeleteReason());
                            activity.setComments(Collections.singletonList(bpmComment));
                        }

                        activities.add(activity);
                    }else{
                        if(StringUtils.equals(instance.getStartUserId(),SecurityUtilz.getUser().getId())){
                            Map<String, Object> vars = processEngine.getRuntimeService().getVariables(instance.getId());
                            bpmInstance.setRevokeAble((Boolean) vars.get("$REVOKE_ABLE"));
                        }
                    }

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

                    String businessKey = instances.stream()
                            .filter(instance -> StringUtils.equals(instance.getId(), task.getProcessInstanceId()))
                            .findFirst()
                            .map(HistoricProcessInstance::getBusinessKey)
                            .orElse(null);

                    if(businessKey!=null){

                        List<String> collect = null;
                        if(StringUtils.isBlank(task.getAssignee())){
                            collect = processEngine.getHistoryService()
                                    .getHistoricIdentityLinksForTask(task.getId())
                                    .stream()
                                    .map(historicIdentityLink -> historicIdentityLink.getUserId())
                                    .collect(Collectors.toList());
                        }

                        taskIndexManager.index(
                                task,
                                collect,
                                task.getEndTime() != null,
                                businessKey
                        );
                    }
                });
    }

    @Override
    public void revoke(String instanceId) {
        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();

        System.out.println(processInstance.getProcessVariables());

        if(StringUtils.isNotBlank(processInstance.getBusinessKey())){
            docService.onBpmKilled(processInstance.getBusinessKey());
        }

        processEngine.getRuntimeService().deleteProcessInstance(instanceId,"撤回流程");
    }

    private AuthenticationContext authenticationContext = new AuthenticationContext(){

        @Override
        public String getAuthenticatedUserId() {
            return SecurityUtilz.getUser().getId();
        }

        @Override
        public Principal getPrincipal() {
            return () -> SecurityUtilz.getUser().getRealname();
        }

        @Override
        public void setPrincipal(Principal principal) {}
    };
}
