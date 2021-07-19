package cn.nkpro.tfms.platform.custom;

import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.ts5.utils.SpringEmulated;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 抽象的组件类
 * Created by bean on 2020/7/31.
 */
public abstract class TfmsAbstractComponent<DDT> implements TfmsComponent {

    @Getter
    protected String componentName;

    public TfmsAbstractComponent(){
        this.componentName = parseComponentName();
    }

    @Override
    public boolean cacheDef() { return true; }

    @Override
    public boolean supports(EnumDocClassify classify) {
        return true;
    }

    @Override
    public String[] getDefComponentNames() {
        return new String[0];
    }

    @Override
    public Object deserializeDef(Object def){
        return parse(def, getType(0));
    }


    @Override
    public final DDT getDef(DefDocTypeBO defDocType) throws Exception {
        return doGetDef(defDocType);
    }
    protected DDT doGetDef(DefDocTypeBO defDocType) throws Exception{
        return null;
    }

    @SuppressWarnings("all")
    @Override
    public final void updateDef(DefDocTypeBO defDocType) throws Exception{
        doUpdateDef(defDocType, (DDT) defDocType.getCustomComponentsDef().get(componentName));
    }
    protected void doUpdateDef(DefDocTypeBO defDocType,DDT def) throws Exception{

    }

    private String parseComponentName(){

        Class<? extends TfmsAbstractComponent> clazz = getClass();

        Component component = clazz.getDeclaredAnnotation(Component.class);
        if(component!=null && StringUtils.isNotBlank(component.value()))
            return component.value();

        Service service = clazz.getDeclaredAnnotation(Service.class);
        if(service!=null && StringUtils.isNotBlank(service.value()))
            return service.value();

        return SpringEmulated.decapitalize(clazz.getSimpleName());
    }


    @SuppressWarnings("all")
    protected final Object parse(Object obj, Type targetType){
        if(targetType!=null && obj!=null){
            // 简单判断一下，因为先转成string再parse效率会比较低
            if(obj instanceof List){
                return new JSONArray((List) obj).toJavaObject(targetType);
            }else if(obj instanceof Map){
                return new JSONObject((Map) obj).toJavaObject(targetType);
            }
            return JSON.parseObject(JSON.toJSONString(obj),targetType);
        }
        return obj;
    }

    protected final Type getType(int index){
        Type[] types = getType(getClass());
        return types !=null && types.length > index ? types[index] : null;
    }

    private final static Map<Class,Type[]> cache = new ConcurrentHashMap<>();

    protected final Type[] getType(Class clazz){
        return cache.computeIfAbsent(clazz,(_clazz)->{
            while(_clazz!=null){
                if(_clazz.getGenericSuperclass()!=null
//                        &&_clazz.getGenericSuperclass().getTypeName().startsWith(TfmsAbstractCard.class.getName())
                ){
                    Type type = _clazz.getGenericSuperclass();
                    if(type instanceof ParameterizedType) {
                        return ((ParameterizedType) type).getActualTypeArguments();
                    }
                }
                _clazz = _clazz.getSuperclass();
            }
            return null;
        });
    }
}
