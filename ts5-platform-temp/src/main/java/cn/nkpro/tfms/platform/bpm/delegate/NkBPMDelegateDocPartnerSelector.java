package cn.nkpro.tfms.platform.bpm.delegate;

import cn.nkpro.tfms.platform.bpm.AbstractNkDelegate;
import cn.nkpro.ts5.exception.TfmsIllegalContentException;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.po.BizDocPartner;
import cn.nkpro.tfms.platform.model.po.BizDocPartnerKey;
import cn.nkpro.tfms.platform.model.po.SysAccount;
import cn.nkpro.tfms.platform.services.TfmsDocEngine;
import cn.nkpro.tfms.platform.services.TfmsSysAccountService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.el.FixedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component("NkBPMDelegateDocPartnerSelector")
public class NkBPMDelegateDocPartnerSelector extends AbstractNkDelegate {
    @Setter
    private FixedValue partnerType;
    @Setter
    private FixedValue variable;
    @Autowired
    private TfmsDocEngine docEngine;
    @Autowired
    private TfmsSysAccountService accountService;
    @Override
    public void execute(DelegateExecution delegateExecution) {

        String docId = delegateExecution.getProcessInstanceBusinessKey();


        BizDocBase docBase = docEngine.getDocDetail(docId);

        @SuppressWarnings("all")
        List<BizDocPartner> partners = (List<BizDocPartner>) docBase.getComponentsData().get("nk-card-doc-partner");

        if(partners==null){
            throw new TfmsIllegalContentException("在["+docBase.getDocName()+"]没有找到交易伙伴组件，不能确定任务待办人");
        }
        String targetType = (String) partnerType.getValue(delegateExecution);

        List<BizDocPartner> collect = partners.stream().filter(bizDocPartner -> StringUtils.equals(bizDocPartner.getType(), targetType))
                .filter(t->StringUtils.isNotBlank(t.getPartnerRoleId()))
                .collect(Collectors.toList());

        if(collect.isEmpty()){
            throw new TfmsIllegalContentException("在["+docBase.getDocName()+"]没有找到类型为["+targetType+"]的交易伙伴，不能确定任务待办人");
        }

        List<String> docIds = collect.stream().map(BizDocPartnerKey::getPartnerRoleId).filter(Objects::nonNull).collect(Collectors.toList());

        List<String> accountIds = accountService.getAccountsByObjectId(docIds).stream().map(SysAccount::getId).collect(Collectors.toList());

        if(accountIds.isEmpty()){
            throw new TfmsIllegalContentException("在["+docBase.getDocName()+"]没有找到类型为["+targetType+"]的用户，不能确定任务待办人");
        }

        delegateExecution.setVariable((String) variable.getValue(delegateExecution), accountIds);
    }
}
