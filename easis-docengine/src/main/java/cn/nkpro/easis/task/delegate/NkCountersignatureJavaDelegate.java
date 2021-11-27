package cn.nkpro.easis.task.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("NkCountersignatureJavaDelegate")
public class NkCountersignatureJavaDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        delegateExecution.setVariable("NK$COUNTERSIGNATURE_USERS", Arrays.asList("nk-default-admin","nk-default-test"));
    }
}
