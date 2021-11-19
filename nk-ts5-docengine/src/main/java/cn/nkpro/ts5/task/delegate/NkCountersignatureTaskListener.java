package cn.nkpro.ts5.task.delegate;

import lombok.Setter;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.el.JuelExpression;
import org.springframework.stereotype.Component;

/**
 * 一个通用的会签结果统计
 *
 *
 * 关于多实例任务
 *      为什么不用 ExecutionListener ？ 因为会执行 实例数量+1 次事件，暂时找不到判断方法，isMultipleRoot 参数似乎不好用
 *
 * 多实例节点私有变量
 *      nrOfInstances：          会签中总共的实例数
 *      nrOfCompletedInstances： 已经完成的实例数量
 *      nrOfActiviteInstances：  当前活动的实例数量，即还没有完成的实例数量
 *
 * 1、会签人员选择：
 * 选择一个 Service Task 节点，并设置
 *      Delegate Expression               = ${NkCountersignatureJavaDelegate}
 *          Other Params                  = 根据实际情况
 *
 * 2、多实例节点配置：
 * 选择一个多实例 User Task 节点，并设置
 *      Assignee 指定任务人                  = ${userId}
 *      Collection 指定会签人员集合           = ${NK$COUNTERSIGNATURE_USERS}
 *      Element Variable 任务人变量          = userId
 *      Completion Condition 会签结束条件    = ${nrOfInstances == nrOfCompletedInstances}
 *      Task Listener                      = ${NkCountersignatureTaskListener}
 *          eventType                      = complete
 *          pass                           = ${nkFlowId=='Flow_0kz1ipm'}
 *
 * 3、多实例节点后的路径走向判断：
 *      通过路线 Expression = ${NK$COUNTERSIGNATURE_PASS_COUNT>=nrOfInstances}
 *      否决路线 Expression = ${NK$COUNTERSIGNATURE_PASS_COUNT< nrOfInstances}
 *
 */
@Component("NkCountersignatureTaskListener")
public class NkCountersignatureTaskListener implements TaskListener {

    /**
     * 注入的通过条件表达式 如 ${nkFlowId=='Flow_0kz1ipm'}
     */
    @Setter
    private JuelExpression pass;

    @Override
    public void notify(DelegateTask delegateTask) {

        //通过数量 var key
        String passCountingKey = "NK$COUNTERSIGNATURE_PASS_COUNT";

        Integer counting = (Integer) delegateTask.getVariable(passCountingKey);
        if (counting==null){
            counting = 0;
        }

        // 如果符合通过条件
        if((Boolean) pass.getValue(delegateTask)){
            counting++;
        }

        // 设置通过计数
        delegateTask.setVariable(passCountingKey,counting);
    }
}
