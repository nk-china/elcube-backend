package cn.nkpro.ts5.task.impl;

import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.service.NkDocEngineFrontService;
import cn.nkpro.ts5.exception.TfmsDefineException;
import cn.nkpro.ts5.docengine.model.BpmTaskTransition;
import cn.nkpro.ts5.elasticearch.SearchEngine;
import cn.nkpro.ts5.docengine.model.BpmTaskES;
import cn.nkpro.ts5.co.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.impl.pvm.PvmActivity;
import org.camunda.bpm.engine.impl.pvm.PvmScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工作流任务的一些公共方法
 */
public abstract class AbstractNkBpmSupport {

    @Autowired@Lazy
    protected NkDocEngineFrontService docEngine;
    @Autowired@Lazy
    protected ProcessEngine processEngine;
    @Autowired@Lazy
    protected SearchEngine searchEngine;

    void indexDocTask(HistoricTaskInstance taskInstance, DocHV doc){
        Map<String, Object> variables = getHistoricVariables(taskInstance.getProcessInstanceId());

        String state;
        Long startTime  = taskInstance.getStartTime().getTime()/1000;
        Long endTime;
        if(taskInstance.getEndTime()==null){
            state   = "create";
            endTime = null;
        }else{
            state   = variables.containsKey("NK$DELETE")?"delete":"complete";// 任务被强制删除
            endTime = taskInstance.getEndTime().getTime()/1000;
        }

        if(doc==null){
            doc = docEngine.detail((String) variables.get("NK$BUSINESS_KEY"));
        }
        BpmTaskES bpmTaskES = BpmTaskES.from(doc,
                taskInstance.getId(),
                taskInstance.getName(),
                taskInstance.getAssignee(),
                state,
                startTime,
                endTime
        );
        searchEngine.indexBeforeCommit(bpmTaskES);
    }

    List<HistoricVariableInstance> getHistoricVariableInstances(String processInstanceId){
        return processEngine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
    }

    private Map<String, Object> getHistoricVariables(String processInstanceId){
        // 获取所有流程变量
        List<HistoricVariableInstance> variables = getHistoricVariableInstances(processInstanceId);

        return variables
                .stream()
                .filter(instance->StringUtils.equals(instance.getExecutionId(),processInstanceId))
                .collect(Collectors.toMap(HistoricVariableInstance::getName,HistoricVariableInstance::getValue));
    }

    // 获取流程图内所有的节点
    List<? extends PvmActivity> getProcessDefinitionActivities(String processDefinitionId){
        return (
                (PvmScope) processEngine.getRepositoryService()
                        .getProcessDefinition(processDefinitionId)
        ).getActivities();
    }

    // 当前任务节点的对外连接线
    List<BpmTaskTransition> getTaskTransition(List<? extends PvmActivity> activities, String taskDefinitionKey){
        // 当前任务节点的对外连接线
        PvmActivity pvmActivity = activities.stream()
                //如果是会签节点，那么 activityId 格式为： activityId#multiInstanceBody
                .filter(activity -> StringUtils.equals(activity.getId().split("#")[0],taskDefinitionKey))
                .findFirst()
                .orElseThrow(()->new TfmsDefineException("没有找到流程任务定义"));

        return pvmActivity.getOutgoingTransitions()
                .stream()
                .map(pvmTransition -> {
                    BpmTaskTransition transition = BeanUtilz.copyFromObject(pvmTransition,BpmTaskTransition.class);
                    transition.setName(StringUtils.firstNonBlank(
                            transition.getName(),
                            (String) pvmTransition.getProperty("name"),
                            transition.getId())
                    );
                    return transition;
                }).collect(Collectors.toList());
    }
}
