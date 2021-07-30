package cn.nkpro.ts5.engine.doc;


import cn.nkpro.ts5.engine.co.NkCustomObject;

/**
 * Created by bean on 2020/7/13.
 */
public interface NkDocStateInterceptor extends NkCustomObject {
    default void apply(){}
}
