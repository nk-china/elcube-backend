package cn.nkpro.tfms.platform.bpm;

import cn.nkpro.tfms.platform.services.TfmsTaskIndexManager;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableActivityCancelledEvent;
import org.flowable.engine.delegate.event.FlowableCancelledEvent;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class TfmsFlowableEventListener extends AbstractFlowableEngineEventListener {

    @Autowired
    private TfmsTaskIndexManager tfmsTaskIndexManager;

    @Override
    public void onEvent(FlowableEvent flowableEvent) {
        super.onEvent(flowableEvent);
    }

    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {

        DelegateExecution execution = super.getExecution(event);
        TaskEntity task = (TaskEntity)event.getEntity();

        tfmsTaskIndexManager.index(task,null,false,execution.getProcessInstanceBusinessKey());
    }

    @Override
    protected void taskCompleted(FlowableEngineEntityEvent event) {

        DelegateExecution execution = super.getExecution(event);
        TaskEntity task = (TaskEntity)event.getEntity();

        tfmsTaskIndexManager.index(task,null,true,execution.getProcessInstanceBusinessKey());
    }

    @Override
    protected void activityCancelled(FlowableActivityCancelledEvent event) {

    }

    @Override
    protected void processCancelled(FlowableCancelledEvent event) {
        try {
            tfmsTaskIndexManager.indexCancel(event.getProcessInstanceId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
