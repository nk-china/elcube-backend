package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.co.NKCustomScriptObject;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.exception.TfmsException;
import cn.nkpro.ts5.utils.ClassUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NKAbstractCard<DT,DDT> extends NKCustomScriptObject implements NKCard<DT,DDT> {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Getter
    private String cardName;

    @Getter
    private String position = NKCard.POSITION_DEFAULT;

    public NKAbstractCard(){
        super();
        this.cardName = Optional.ofNullable(getClass().getAnnotation(WsDocNote.class))
                .map(WsDocNote::value)
                .orElse(beanName);
    }

    @Override
    public String desc() {
        return beanName + " | " + cardName;
    }

    public String getDataComponentName() {
        return beanName;
    }

    public String[] getDefComponentNames() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }


    /**
     * 预解析卡片配置
     */
    @Override
    @SuppressWarnings("unchecked")
    public final DDT deserializeDef(DocDefIV docDefI){
        return (DDT) parse(docDefI.getCardContent(),getType(1));
    }

    public DDT afterGetDef(DocDefHV defHV, DocDefIV defIV, DDT def){
        return def;
    }

    /**
     * 预解析数据，并转换成DT类型
     */
    @Override
    @SuppressWarnings("all")
    public final DT deserialize(Object data){
        return (DT) parse(data,getType(0));
    }

    @Override
    public DT afterCreate(DocHV doc, DocHV preDoc, DT data, DDT def){
        return data;
    }


    @Override
    public DT afterGetData(DocHV doc, DT data, DDT def) {
        return data;
    }

    /**
     * 将数据进行一次计算，再返回
     * @param doc
     * @param docDef
     * @return
     * @throws Exception
     */
    @Override
    @SuppressWarnings("all")
    public final DT calculate(DocHV doc, DT data, DDT def, boolean isTrigger, String options){
        return data;
    }

    @Override
    public DT beforeUpdate(DocHV doc, DT data, DDT def, DT original) {
        return data;
    }

    @SuppressWarnings("all")
    protected final Object parse(Object obj, Type targetType) {

        if(targetType!=null){
            if(obj==null){

                Class<?> clazz = (Class)targetType;
                if(clazz.isInterface()){
                    if(List.class.isAssignableFrom(clazz)){
                        return new ArrayList<>();
                    }
                    if(Map.class.isAssignableFrom(clazz)){
                        return new HashMap<>();
                    }
                    throw new TfmsException("卡片数据类型不支持 "+clazz.getName());
                }else{
                    try {
                        return ((Class)targetType).getConstructor().newInstance();
                    } catch (Exception e) {
                        throw new TfmsException("卡片数据类型必须声明空构造方法 "+e.getMessage(),e);
                    }
                }

            }else{
                // 简单判断一下，因为先转成string再parse效率会比较低
                if(obj instanceof String){
                    return JSON.parseObject((String)obj,targetType);
                }else if(obj instanceof List){
                    return new JSONArray((List) obj).toJavaObject(targetType);
                }else if(obj instanceof Map){
                    return new JSONObject((Map) obj).toJavaObject(targetType);
                }
                return JSON.parseObject(JSON.toJSONString(obj),targetType);
            }
        }
        throw new TfmsException("不能解析卡片对象["+getClass()+"]的数据类型");
    }

    private final static Map<Class,Type[]> cache = new ConcurrentHashMap<>();

    private Type getType(int index){
        Type[] types = getType(getClass());
        return types !=null && types.length > index ? types[index] : null;
    }

    private Type[] getType(Class clazz){
        return cache.computeIfAbsent(clazz,(_clazz)->{
            while(_clazz!=null){
                if(_clazz.getGenericSuperclass()!=null){
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
