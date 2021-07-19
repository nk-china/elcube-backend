package cn.nkpro.tfms.platform.custom.interceptor;

import cn.nkpro.tfms.platform.basis.TfmsCustomObject;

/**
 * Created by bean on 2020/7/13.
 */
public interface TfmsDocStateInterceptor extends TfmsCustomObject {
    default void apply(){}
}
