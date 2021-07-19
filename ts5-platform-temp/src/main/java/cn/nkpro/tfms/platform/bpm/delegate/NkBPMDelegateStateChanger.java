package cn.nkpro.tfms.platform.bpm.delegate;

import cn.nkpro.tfms.platform.bpm.AbstractNkDelegate;
import cn.nkpro.ts5.exception.TfmsIllegalContentException;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.services.ThreadLocalContextHolder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.el.FixedValue;
import org.springframework.stereotype.Component;

/**
 * Created by bean on 2019/12/19.
 *
 * 将单据的状态修改为指定值
 */
@Slf4j
@Component("NkDelegateStateChange")
public class NkBPMDelegateStateChanger extends AbstractNkDelegate {

    /**
     * 注入目标状态值
     */
    @Setter
    private FixedValue state;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        String targetState = (String) state.getValue(delegateExecution);

//        String businessKey = processEngine.getRuntimeService()
//                .createProcessInstanceQuery()
//                .processInstanceId(delegateExecution.getProcessInstanceId())
//                .singleResult()
//                .getBusinessKey();

        BizDocBase doc = ThreadLocalContextHolder.getRuntimeDoc();

        if(doc!=null){
            doc.setDocState(targetState);
            log.info("单据状态已修改为：{}",targetState);
        }else{
            throw new TfmsIllegalContentException("该Delegate只能运行在工作流启动时");
        }
    }
}
