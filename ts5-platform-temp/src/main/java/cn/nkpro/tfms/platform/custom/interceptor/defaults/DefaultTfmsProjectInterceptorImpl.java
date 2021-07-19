package cn.nkpro.tfms.platform.custom.interceptor.defaults;

import cn.nkpro.tfms.platform.custom.interceptor.TfmsProjectInterceptor;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizProjectBO;
import org.springframework.stereotype.Component;

/**
 * Created by bean on 2020/7/13.
 */
@Component
public class DefaultTfmsProjectInterceptorImpl implements TfmsProjectInterceptor {
    @Override
    public void creating(BizProjectBO project) {

    }

    @Override
    public void updating(BizProjectBO project) {

    }

    @Override
    public void updated(BizProjectBO project) {

    }

    @Override
    public void updateCommitted(BizProjectBO project) {

    }

    @Override
    public void stateChanged(BizProjectBO project, String oldProjectState) {

    }

    @Override
    public void docCreating(BizProjectBO project, BizDocBase doc) {

    }

    @Override
    public void docCreated(BizProjectBO project, BizDocBase doc) {

    }
}
