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
 * 设置工作流是否允许撤销
 */
@Slf4j
@Component("NkDelegateRevokeAbleUpdate")
public class NkBPMDelegateRevokeAbleUpdater extends AbstractNkDelegate {

    /**
     * 注入目标
     */
    @Setter
    private FixedValue sign;

    @Override
    public void execute(DelegateExecution delegateExecution) {

        String signValue = (String) sign.getValue(delegateExecution);

        delegateExecution.setVariable("$REVOKE_ABLE",Boolean.parseBoolean(signValue));

        log.info("设置流程是否允许撤销：{}",signValue);
    }
}
