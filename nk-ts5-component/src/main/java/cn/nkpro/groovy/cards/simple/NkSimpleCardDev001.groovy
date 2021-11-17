package cn.nkpro.groovy.cards.simple

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.co.NkCustomObjectManager
import cn.nkpro.ts5.co.remote.NkRemoteAdapter
import cn.nkpro.ts5.co.spel.NkSpELManager
import cn.nkpro.ts5.docengine.NkAbstractCard
import cn.nkpro.ts5.docengine.NkDocEngine
import cn.nkpro.ts5.docengine.model.DocDefHV
import cn.nkpro.ts5.docengine.model.DocDefIV
import cn.nkpro.ts5.docengine.model.DocHV
import cn.nkpro.ts5.docengine.model.easy.EasySingle
import cn.nkpro.ts5.docengine.service.NkDocEngineContext
import cn.nkpro.ts5.exception.NkDefineException
import com.alibaba.fastjson.JSON
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

import java.lang.reflect.Array

@NkNote("演示标准配置卡片")
@Component("NkSimpleCardDev001")
class NkSimpleCardDev001 extends NkAbstractCard<NkCardSimpleData,NkCardSimpleDef> {

    @Autowired
    NkDocEngine docEngine

    @Autowired
    NkCustomObjectManager customObjectManager

    @Autowired
    NkSpELManager spELManager

    @Override
    Object callDef(NkCardSimpleDef nkCardSimpleDef, Object options) {

        if(Objects.equals(options,"listedAdapters"))
            return customObjectManager.getCustomObjectDescriptionList(NkRemoteAdapter.class, true, null)

        return super.callDef(nkCardSimpleDef, options)
    }

    @Override
    NkCardSimpleDef afterGetDef(DocDefHV defHV, DocDefIV defIV, NkCardSimpleDef d) {

        d.getFields()
                .findAll { e -> StringUtils.equals(e.getType(),"select") }
                .findAll { e -> StringUtils.isNotBlank(e.getOptions() as CharSequence) }
                .forEach { e -> e.setOptions(JSON.parseArray(spELManager.convert(e.getOptions() as String, null))) }

        return super.afterGetDef(defHV, defIV, d)
    }

    @Override
    NkCardSimpleData afterCreate(DocHV doc, DocHV preDoc, NkCardSimpleData data, DocDefIV defIV, NkCardSimpleDef d) {

        this.runSpEL(EasySingle.from(data), doc, d.getFields(), defIV.getCardKey(), true)

        return super.afterCreate(doc, preDoc, data, defIV, d)
    }

    @Override
    NkCardSimpleData calculate(DocHV doc, NkCardSimpleData data, DocDefIV defIV, NkCardSimpleDef d, boolean isTrigger, Object options) {

        if(Objects.equals(options,"search")){

            if(StringUtils.isNotBlank(d.getRemoteAdapter())){

                Map params = null

                if(StringUtils.isNotBlank(d.getRemoteAdapterParamsSpEL())){
                    def context = spELManager.createContext(doc)
                    context.setVariable("self",data)
                    params = spELManager.invoke(d.getRemoteAdapterParamsSpEL(), context) as Map
                }

                Map ret = customObjectManager.getCustomObject(d.getRemoteAdapter(), NkRemoteAdapter.class)
                    .apply(params, Map.class) as Map

                data.setAge(ret.get("age") as Integer)
            }
        }

        this.runSpEL(EasySingle.from(data), doc, d.getFields(), defIV.getCardKey(), false)

        return data
    }

    private void runSpEL(EasySingle data, DocHV doc, List<NkCardSimpleDefField> fields, String cardKey, boolean isNewCreate){

        EvaluationContext context = spELManager.createContext(doc)



        fields.stream()
            .sorted({ o1, o2 ->
                return NumberUtils.compare(o1.getCalcOrder(),o2.getCalcOrder())
            })
            .forEach{ field ->
                if(StringUtils.isNotBlank(field.getSpELContent())){

                    String trigger = null;
                    if(ArrayUtils.contains(field.getSpELTriggers(),"ALWAYS")){
                        trigger = "ALWAYS";
                    }else if(ArrayUtils.contains(field.getSpELTriggers(),"INIT") && isNewCreate){
                        trigger = "INIT";
                    }else if(ArrayUtils.contains(field.getSpELTriggers(),"BLANK") && isBlank(data.get(field.getKey()))){
                        trigger = "BLANK";
                    }

                    if(trigger!=null){

                        if(log.isInfoEnabled())
                            log.info("{}\t\t{} 执行表达式 KEY={} T={} EL={}",
                                    NkDocEngineContext.currLog(),
                                    cardKey,
                                    field.getKey(),
                                    trigger,
                                    field.getSpELContent()
                            );

                        try{
                            data.set(field.getKey(),spELManager.invoke(field.getSpELContent(),context));
                        }catch(Exception e){
                            throw new NkDefineException(
                                    String.format("KEY=%s T=%s %s",
                                            field.getKey(),
                                            trigger,
                                            e.getMessage()
                                    )
                            );
                        }
                    }
                }
                context.setVariable(field.getKey(), data.get(field.getKey()))
            }
    }

    private static boolean isBlank(Object value){
        return value == null ||
                (value instanceof Array && Array.getLength(value)==0) ||
                (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value)) ||
                StringUtils.isBlank(value.toString());
    }

    static class NkCardSimpleData {

        private String name
        private Integer age
        private Integer sex
        private String likes

        String getName() {
            return name
        }

        void setName(String name) {
            this.name = name
        }

        Integer getAge() {
            return age
        }

        void setAge(Integer age) {
            this.age = age
        }

        Integer getSex() {
            return sex
        }

        void setSex(Integer sex) {
            this.sex = sex
        }

        String getLikes() {
            return likes
        }

        void setLikes(String likes) {
            this.likes = likes
        }
    }

    static class NkCardSimpleDefField{
        private String key
        private String label
        private String type
        private Boolean visible
        private Boolean readonly
        private String[] spELTriggers
        private String spELContent
        private int calcOrder
        private String calcTrigger
        private Object options

        String getKey() {
            return key
        }

        void setKey(String key) {
            this.key = key
        }

        String getLabel() {
            return label
        }

        void setLabel(String label) {
            this.label = label
        }

        String getType() {
            return type
        }

        void setType(String type) {
            this.type = type
        }

        Boolean getVisible() {
            return visible
        }

        void setVisible(Boolean visible) {
            this.visible = visible
        }

        Boolean getReadonly() {
            return readonly
        }

        void setReadonly(Boolean readonly) {
            this.readonly = readonly
        }

        String[] getSpELTriggers() {
            return spELTriggers
        }

        void setSpELTriggers(String[] spELTriggers) {
            this.spELTriggers = spELTriggers
        }

        String getSpELContent() {
            return spELContent
        }

        void setSpELContent(String spELContent) {
            this.spELContent = spELContent
        }

        int getCalcOrder(){
            return this.calcOrder
        }

        void setCalcOrder(int calcOrder) {
            this.calcOrder = calcOrder
        }

        String getCalcTrigger() {
            return calcTrigger
        }

        void setCalcTrigger(String calcTrigger) {
            this.calcTrigger = calcTrigger
        }

        Object getOptions() {
            return options
        }

        void setOptions(Object options) {
            this.options = options
        }
    }

    static class NkCardSimpleDef {

        private String remoteAdapter;

        private String remoteAdapterParamsSpEL;

        private List<NkCardSimpleDefField> fields

        String getRemoteAdapter() {
            return remoteAdapter
        }

        void setRemoteAdapter(String remoteAdapter) {
            this.remoteAdapter = remoteAdapter
        }

        String getRemoteAdapterParamsSpEL() {
            return remoteAdapterParamsSpEL
        }

        void setRemoteAdapterParamsSpEL(String remoteAdapterParamsSpEL) {
            this.remoteAdapterParamsSpEL = remoteAdapterParamsSpEL
        }

        List<NkCardSimpleDefField> getFields() {
            return fields
        }

        void setFields(List<NkCardSimpleDefField> fields) {
            this.fields = fields
        }
    }
}
