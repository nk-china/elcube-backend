package cn.nkpro.easis.docengine.interceptor;


import cn.nkpro.easis.co.NkCustomObject;

/**
 * Created by bean on 2020/7/13.
 */
public interface NkDocStateInterceptor extends NkCustomObject {
    default void apply(){}
}
