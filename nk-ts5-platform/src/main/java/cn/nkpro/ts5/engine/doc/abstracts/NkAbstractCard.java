package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.config.NkProperties;
import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.NkCard;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.model.ScriptDefHV;
import cn.nkpro.ts5.exception.TfmsDefineException;
import cn.nkpro.ts5.exception.TfmsSystemException;
import cn.nkpro.ts5.orm.mb.gen.SysLogDocRecord;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class NkAbstractCard<DT,DDT> extends NkAbstractCustomScriptObject implements NkCard<DT,DDT> {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Getter
    private String cardName;

    @Getter
    private String position = NkCard.POSITION_DEFAULT;

    @Autowired
    private NkProperties properties;

    public NkAbstractCard(){
        super();
        this.cardName = Optional.ofNullable(getClass().getAnnotation(WsDocNote.class))
                .map(WsDocNote::value)
                .orElse(beanName);
    }

    @Override
    public boolean isDebug() {
        return getScriptDef().isDebug();
    }

    @Override
    public String desc() {
        return beanName + " | " + cardName;
    }

    @Override
    public String getDataComponentName() {
        if(StringUtils.isNotBlank(scriptDef.getVueMain())){
            return beanName;
        }
        return null;
    }

    @Override
    public final String[] getAutoDefComponentNames() {

        ScriptDefHV scriptDef = scriptDefHV();

        if(scriptDef!=null && StringUtils.isNotBlank(scriptDef.getVueDefs())){
            JSONArray array = JSON.parseArray(scriptDef.getVueDefs());
            String[] names = new String[array.size()];
            for(int i=0;i < array.size();i++){
                names[i]=getBeanName()+(i==0?"Def":("Def"+i));
            }
            return names;
        }

        return getDefComponentNames();
    }

    private ScriptDefHV scriptDefHV(){
        if(properties.isComponentReloadClassPath()){
            ScriptDefHV defHV = super.loadScriptFromClassPath();
            if(defHV!=null){
                return defHV;
            }
        }
        return this.scriptDef;
    }

    @Override
    public Map<String,String> getVueTemplate(){

        ScriptDefHV scriptDef = scriptDefHV();

        if(scriptDef!=null){
            Map<String,String> vueMap = new HashMap<>();
            if(StringUtils.isNotBlank(scriptDef.getVueMain())){
                vueMap.put(scriptDef.getScriptName(),scriptDef.getVueMain());
            }
            if(StringUtils.isNotBlank(scriptDef.getVueDefs())){
                JSONArray array = JSON.parseArray(scriptDef.getVueDefs());
                array.forEach((item)->{
                    int index = array.indexOf(item);
                    vueMap.put(scriptDef.getScriptName()+"Def"+(index==0?"":index), (String) item);
                });
            }
            return vueMap;
        }
        return Collections.emptyMap();
    }

    @SuppressWarnings("all")
    protected String[] getDefComponentNames() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }


    /**
     * 预解析卡片配置
     */
    @Override
    @SuppressWarnings("unchecked")
    public final DDT deserializeDef(Object defContent){
        return (DDT) parse(defContent,getType(1));
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
    public DT afterCreate(DocHV doc, DocHV preDoc, DT data, DocDefIV defIV, DDT def){
        return data;
    }


    @Override
    public DT afterGetData(DocHV doc, DT data, DocDefIV defIV, DDT def) {
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
    public DT calculate(DocHV doc, DT data, DocDefIV defIV, DDT def, boolean isTrigger, String options){
        return data;
    }

    @Override
    public DT call(DocHV doc, DT data, DocDefIV defIV, DDT def, String options) {
        return null;
    }

    @Override
    public DT beforeUpdate(DocHV doc, DT data, DT original, DocDefIV defIV, DDT def) {
        return data;
    }

    @Override
    public DT afterUpdated(DocHV doc, DT data, DT original, DocDefIV defIV, DDT def) {
        return data;
    }

    @Override
    public void stateChanged(DocHV doc, DocHV original, DT data, DocDefIV defIV, DDT def){

    }

    @SuppressWarnings("all")
    protected final Object parse(Object obj, Type targetType) {

        if(targetType!=null){
            if(obj==null){

                Class<?> clazz = null;
                if(targetType instanceof ParameterizedType){
                    clazz = (Class<?>) ((ParameterizedType) targetType).getRawType();
                }else if(targetType instanceof Class){
                    clazz = (Class)targetType;
                }else{
                    throw TfmsSystemException.of("未知的参数类型："+targetType);
                }

                if(clazz.isInterface()){
                    if(List.class.isAssignableFrom(clazz)){
                        return new ArrayList<>();
                    }
                    if(Map.class.isAssignableFrom(clazz)){
                        return new HashMap<>();
                    }
                    throw new TfmsDefineException("卡片数据类型不支持 "+clazz.getName());
                }else{
                    try {
                        return ((Class)targetType).getConstructor().newInstance();
                    } catch (Exception e) {
                        throw new TfmsDefineException("卡片数据类型必须声明空构造方法 "+e.getMessage(),e);
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
        throw new TfmsDefineException("不能解析卡片对象["+getClass()+"]的数据类型");
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
