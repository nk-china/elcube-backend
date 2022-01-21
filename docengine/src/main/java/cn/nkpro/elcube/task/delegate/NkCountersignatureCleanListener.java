package cn.nkpro.elcube.task.delegate;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * 会签结果清零
 */
@Component("NkCountersignatureCleanListener")
public class NkCountersignatureCleanListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        //通过数量 var key
        String passCountingKey = "NK$COUNTERSIGNATURE_PASS_COUNT";
        // 设置通过计数
        delegateTask.setVariable(passCountingKey,0);
    }
}