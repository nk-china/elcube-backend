package cn.nkpro.tfms.platform.bpm.listener;

import lombok.Setter;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.impl.el.FixedValue;
import org.flowable.engine.impl.el.JuelExpression;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
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
 *      Delegate Expression               = ${NkBPMDelegateAssigneeSelector}
 *          Other Params                  = 根据实际情况
 *
 * 2、多实例节点配置：
 * 选择一个多实例 User Task 节点，并设置
 *      Assignee 指定任务人                  = ${userId}
 *      Collection 指定会签人员集合           = ${users}
 *      Element Variable 任务人变量          = userId
 *      Completion Condition 会签结束条件    = ${nrOfInstances == nrOfCompletedInstances}
 *      Task Listener                      = ${NkBPMMultipleTaskListener}
 *          passCountingVariable           = pass
 *          sumCountingVariable            = sum
 *          passCounting                   = ${nkFlowId=='Flow_0kz1ipm'}
 *
 * 3、多实例节点后的路径走向判断：
 *      通过路线 Expression = ${pass>=sum}
 *      否决路线 Expression = ${pass< sum}
 *
 */
@Component("NkBPMMultipleTaskListener")
public class NkBPMMultipleTaskListener implements TaskListener {

    /**
     * 通过数量 var key
     */
    @Setter
    private FixedValue passCountingVariable;
    /**
     * 会签总任务数的 var key
     */
    @Setter
    private FixedValue sumCountingVariable;

    private Expression expression;

    /**
     * 注入的通过条件表达式 如 ${nkFlowId=='Flow_0kz1ipm'}
     */
    @Setter
    private JuelExpression passCounting;

    @Override
    public void notify(DelegateTask delegateTask) {

        String varSumCounting = (String) sumCountingVariable.getValue(delegateTask);
        String varPassCounting = (String) passCountingVariable.getValue(delegateTask);

        Integer counting = delegateTask.getVariable(varPassCounting,Integer.class);
        counting = counting==null?0:counting;
        if((Boolean) passCounting.getValue(delegateTask)){
            counting++;
        }
        // 设置通过计数
        delegateTask.setVariable(varPassCounting,counting);
        // 设置会签计数
        delegateTask.setVariable(varSumCounting,delegateTask.getVariable("nrOfInstances"));
    }
}
