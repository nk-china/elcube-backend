package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.orm.mb.gen.DocDefI;
import cn.nkpro.ts5.utils.SpringEmulated;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NKAbstractCard<DT,DDT> implements NKCard<DT,DDT> {

    @Getter
    protected String cardHandler;

    @Getter
    protected String cardName;

    public NKAbstractCard(){

        this.cardHandler = parseComponentName();
        this.cardName = Optional.ofNullable(getClass().getAnnotation(WsDocNote.class))
                .map(WsDocNote::value)
                .orElse(cardHandler);
    }

    @Override
    public String desc() {
        return cardHandler + " | " + cardName;
    }

    public String getDataComponentName() {
        return cardHandler;
    }

    public String[] getDefComponentNames() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    /**
     * 单据创建时调用，初始化卡片数据，返回一个新的DT对象
     * @param doc
     * @param preDoc
     * @param docDef
     * @return
     * @throws Exception
     */
    @SuppressWarnings("all")

    @Override
    public final DT create(DocHV doc, DocHV preDoc, DocDefI docDef) throws Exception{
        Class<DT> typeDT = (Class<DT>) getType(0);

        if(typeDT == Map.class){
            return (DT) new HashMap<>();
        }
        if(typeDT == List.class){
            return (DT) new ArrayList<>();
        }

        return typeDT.getConstructor().newInstance();
    }

    @Override
    public DT afterCreate(DocHV doc, DocHV preDoc, DT data, DDT def){
        return data;
    }

    /**
     * 加载数据，并转换成DT类型
     * @param doc
     * @return
     * @throws Exception
     */
    @Override
    @SuppressWarnings("all")
    public final DT getData(DocHV doc, DocDefI defI) throws Exception {
        // todo 从数据库中加载原始JSON数据
        return (DT) doc.getData().get(defI.getCardKey());
    }

    @Override
    @SuppressWarnings("all")
    public DT afterGetData(DocHV doc, String data, String def){
        return (DT) parse(data,getType(0));
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
    public final DT calculate(DocHV doc, DocDefI defI,String options) throws Exception{
        return (DT) doc.getData().get(defI.getCardKey());
    }

    /**
     * 将数据转换成JSON，并存入DB
     * @param doc
     * @param docDef
     * @param original
     * @throws Exception
     */
    @SuppressWarnings("all")
    public final void update(DocHV doc, DocDefHV docDef, DocHV original) throws Exception{
        // todo
    }


    public final Object deserializeDef(Object def){
        return parse(def, getType(1));
    }

    public final Object deserialize(Object data){
        return parse(data, getType(0));
    }

    @SuppressWarnings("all")
    protected final Object parse(Object obj, Type targetType){
        if(targetType!=null && obj!=null){
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
        return obj;
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



    private String parseComponentName(){

        Class<? extends NKAbstractCard> clazz = getClass();

        Component component = clazz.getDeclaredAnnotation(Component.class);
        if(component!=null && StringUtils.isNotBlank(component.value()))
            return component.value();

        Service service = clazz.getDeclaredAnnotation(Service.class);
        if(service!=null && StringUtils.isNotBlank(service.value()))
            return service.value();

        return SpringEmulated.decapitalize(clazz.getSimpleName());
    }
}
