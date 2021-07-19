package cn.nkpro.tfms.platform.custom.interceptor;

import cn.nkpro.tfms.platform.basis.TfmsCustomObject;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizProjectBO;

/**
 * Created by bean on 2020/7/13.
 */
public interface TfmsProjectInterceptor extends TfmsCustomObject {

    default void creating(BizProjectBO project){}

    default void updating(BizProjectBO project){}

    default void updated(BizProjectBO project){}

    default void updateCommitted(BizProjectBO project){}

    default void stateChanged(BizProjectBO project, String oldProjectState){}

    default void docCreating(BizProjectBO project, BizDocBase doc){}

    default void docCreated(BizProjectBO project, BizDocBase doc){}
}
