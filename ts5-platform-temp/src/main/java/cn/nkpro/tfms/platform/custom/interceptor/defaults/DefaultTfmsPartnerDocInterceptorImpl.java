package cn.nkpro.tfms.platform.custom.interceptor.defaults;

import cn.nkpro.tfms.platform.custom.interceptor.TfmsPartnerDocInterceptor;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizPartnerBO;
import org.springframework.stereotype.Component;

/**
 * Created by bean on 2020/7/22.
 */
@Component
public class DefaultTfmsPartnerDocInterceptorImpl implements TfmsPartnerDocInterceptor {

    @Override
    public boolean preCondition(BizPartnerBO partner, BizDocBase preRole) {
        return true;
    }
}
