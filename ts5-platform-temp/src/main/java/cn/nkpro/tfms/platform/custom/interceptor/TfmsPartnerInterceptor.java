package cn.nkpro.tfms.platform.custom.interceptor;

import cn.nkpro.tfms.platform.basis.TfmsCustomObject;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizPartnerBO;

/**
 * Created by bean on 2020/7/13.
 */
public interface TfmsPartnerInterceptor extends TfmsCustomObject {

    default void creating(BizPartnerBO partner){}

    default void updating(BizPartnerBO partner){}

    default void updated(BizPartnerBO partner){}

    default void updateCommitted(BizPartnerBO partner){}

    default void roleCreating(BizPartnerBO partner, BizDocBase role){}

    default void roleCreated(BizPartnerBO partner, BizDocBase role){}
}
