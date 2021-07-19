package cn.nkpro.tfms.platform.custom.interceptor;

import cn.nkpro.tfms.platform.basis.TfmsCustomObject;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizPartnerBO;

/**
 * Created by bean on 2020/7/13.
 */
public interface TfmsPartnerDocInterceptor extends TfmsCustomObject {

    default boolean preCondition(BizPartnerBO partner, BizDocBase preRole){
        return true;
    }
}
