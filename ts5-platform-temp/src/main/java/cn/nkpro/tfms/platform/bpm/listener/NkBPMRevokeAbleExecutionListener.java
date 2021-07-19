package cn.nkpro.tfms.platform.bpm.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

@Component("NkBPMRevokeAbleExecutionListener")
public class NkBPMRevokeAbleExecutionListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution delegateExecution) {
        delegateExecution.setVariable("$REVOKE_ABLE",true);
    }
}
