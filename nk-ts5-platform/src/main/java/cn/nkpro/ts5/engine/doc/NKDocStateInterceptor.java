package cn.nkpro.ts5.engine.doc;


import cn.nkpro.ts5.engine.co.NKCustomObject;

/**
 * Created by bean on 2020/7/13.
 */
public interface NKDocStateInterceptor extends NKCustomObject {
    default void apply(){}
}
