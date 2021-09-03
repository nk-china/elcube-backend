package cn.nkpro.ts5.docengine.interceptor;


import cn.nkpro.ts5.co.NkCustomObject;

/**
 * Created by bean on 2020/7/13.
 */
public interface NkDocStateInterceptor extends NkCustomObject {
    default void apply(){}
}
