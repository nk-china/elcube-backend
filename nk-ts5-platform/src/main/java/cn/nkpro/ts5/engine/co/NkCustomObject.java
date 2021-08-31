package cn.nkpro.ts5.engine.co;

/**
 * Created by bean on 2020/7/16.
 */
public interface NkCustomObject {

    default String getBeanName(){return getClass().getName();}

    default boolean isFinal(){
        return false;
    }

    default String desc() {
        return getBeanName();
    }
}
