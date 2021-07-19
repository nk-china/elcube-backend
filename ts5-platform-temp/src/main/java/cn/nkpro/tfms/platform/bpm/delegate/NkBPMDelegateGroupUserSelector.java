package cn.nkpro.tfms.platform.bpm.delegate;

import cn.nkpro.tfms.platform.bpm.AbstractNkDelegate;
import cn.nkpro.tfms.platform.mappers.gen.SysAuthGroupRefMapper;
import cn.nkpro.tfms.platform.model.po.SysAuthGroupRefExample;
import cn.nkpro.tfms.platform.model.po.SysAuthGroupRefKey;
import cn.nkpro.tfms.platform.services.TfmsPermService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("NkBPMDelegateGroupUserSelector")
public class NkBPMDelegateGroupUserSelector extends AbstractNkDelegate {
    @Setter
    private FixedValue groupId;
    @Setter
    private FixedValue variable;
    @Autowired
    private SysAuthGroupRefMapper authGroupRefMapper;
    @Override
    public void execute(DelegateExecution delegateExecution) {
        // 查询用户组下的账号
        SysAuthGroupRefExample authGroupRefExample = new SysAuthGroupRefExample();
        authGroupRefExample.createCriteria()
                .andGroupIdEqualTo((String) groupId.getValue(delegateExecution))
                .andRefTypeEqualTo(TfmsPermService.GROUP_TO_ACCOUNT);

        List<String> accountIds = authGroupRefMapper.selectByExample(authGroupRefExample)
                .stream()
                .map(SysAuthGroupRefKey::getRefId)
                .collect(Collectors.toList());

        delegateExecution.setVariable((String) variable.getValue(delegateExecution), accountIds);
    }
}
