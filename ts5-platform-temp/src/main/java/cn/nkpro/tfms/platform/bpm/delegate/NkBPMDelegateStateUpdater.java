package cn.nkpro.tfms.platform.bpm.delegate;

import cn.nkpro.tfms.platform.bpm.AbstractNkDelegate;
import cn.nkpro.tfms.platform.model.BizDocBase;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.el.FixedValue;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

/**
 * Created by bean on 2019/12/19.
 *
 * 将单据的状态修改为指定值
 */
@Slf4j
@Component("NkDelegateStateUpdate")
public class NkBPMDelegateStateUpdater extends AbstractNkDelegate {

    /**
     * 注入目标状态值
     */
    @Setter
    private FixedValue state;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        String targetState = (String) state.getValue(delegateExecution);

        ProcessInstance processInstance = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(delegateExecution.getProcessInstanceId())
                .singleResult();

        String businessKey = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(delegateExecution.getProcessInstanceId())
                .singleResult()
                .getBusinessKey();

        BizDocBase doc = docEngine.getDocDetail(businessKey);
        doc.setDocState(targetState);

        docEngine.doUpdate(doc,"Update By BPM ["+processInstance.getProcessDefinitionName()+"]");
        log.info("单据状态已修改为：{}",targetState);
    }
}
