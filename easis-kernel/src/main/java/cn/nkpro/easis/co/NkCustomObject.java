package cn.nkpro.easis.co;

import cn.nkpro.easis.annotation.NkNote;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.Order;

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

    default int order(){
        Order order = AopUtils.getTargetClass(this).getAnnotation(Order.class);
        if(order!=null){
            return order.value();
        }
        return Integer.MAX_VALUE;
    }
}
