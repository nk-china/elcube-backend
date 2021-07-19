package cn.nkpro.tfms.platform.bpm.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Component("NkBPMUnRevokeAbleExecutionListener")
public class NkBPMUnRevokeAbleExecutionListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) {
        delegateExecution.removeVariable("$REVOKE_ABLE");
    }
}
