package cn.nkpro.easis.co.remote;

import cn.nkpro.easis.co.NkAbstractCustomScriptObject;
import cn.nkpro.easis.utils.BeanUtilz;
import cn.nkpro.easis.utils.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class NkAbstractRemoteAdapter<R,T> extends NkAbstractCustomScriptObject implements NkRemoteAdapter<R,T> {

    @Override
    public <S> S apply(Object options, Class<S> returnType) {

        if(options!=null){
            Type superclass = getClass().getGenericSuperclass();
            if(superclass instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) superclass).getActualTypeArguments()[0];
                if(rawType instanceof ParameterizedType){
                    rawType = ((ParameterizedType)rawType).getRawType();
                }

                if(!(rawType==options.getClass()||(rawType instanceof Class && ((Class) rawType).isInterface() && ClassUtils.hasInterface(options.getClass(), (Class<?>) rawType)))){
                    System.out.println(rawType);
                    System.out.println(options.getClass());
                    options = BeanUtilz.cloneWithFastjson(options, rawType);
                }
            }
        }

        @SuppressWarnings("all")
        Object apply = this.doApply((R) options);
        return BeanUtilz.cloneWithFastjson(apply, returnType);
    }

    public abstract Object doApply(R options);
}
