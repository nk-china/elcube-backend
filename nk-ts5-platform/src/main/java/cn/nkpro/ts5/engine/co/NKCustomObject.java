package cn.nkpro.ts5.engine.co;

/**
 * Created by bean on 2020/7/16.
 */
public interface NKCustomObject {
    default boolean isFinal(){
        return false;
    }
    default String desc() {
        return getClass().getSimpleName();
    }
}
