package cn.nkpro.ts5.co;

import cn.nkpro.ts5.annotation.NkNote;

/**
 * Created by bean on 2020/7/16.
 */
public interface NkCustomObject {

    default String getBeanName(){return getClass().getName();}

    default boolean isFinal(){
        return false;
    }

    default String desc() {

        NkNote nkNote = this.getClass().getAnnotation(NkNote.class);
        if(nkNote!=null)
            return getBeanName() + " | " + nkNote.value();

        return getBeanName();
    }
}
