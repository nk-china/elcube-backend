package cn.nkpro.ts5.engine.task.delegate;

import lombok.Setter;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.springframework.stereotype.Component;

@Component("NkAssigneeExecutionListener")
public class NkAssigneeExecutionListener implements ExecutionListener {
    @Setter
    private FixedValue var;
    @Override
    public void notify(DelegateExecution delegateExecution) {
        String varValue = (String) var.getValue(delegateExecution);
        delegateExecution.setVariableLocal(varValue,"nk-default-admin");
    }
}