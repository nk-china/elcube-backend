package cn.nkpro.groovy.cards.universal

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.engine.doc.abstracts.NkAbstractCard
import cn.nkpro.ts5.engine.doc.impl.NkDocEngineContext
import cn.nkpro.ts5.engine.doc.model.DocDefIV
import cn.nkpro.ts5.engine.doc.model.DocHV
import cn.nkpro.ts5.engine.spel.TfmsSpELManager
import cn.nkpro.ts5.exception.TfmsDefineException
import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

import java.lang.reflect.Array

@SuppressWarnings("unused")
@WsDocNote("基础表单")
@Component("NkCardForm")
class NkCardForm extends NkAbstractCard<Map,NkCardFormDef> {

    @Autowired
    private TfmsSpELManager spELManager

    @Override
    Map afterCreate(DocHV doc, DocHV preDoc, Map data, DocDefIV defIV, NkCardFormDef d) {
        return execSpEL(doc, data, defIV, d, true)
    }

    @Override
    Map afterGetData(DocHV doc, Map data, DocDefIV defIV, NkCardFormDef d) {
        return execSpEL(doc, data, defIV, d, false)
    }

    @Override
    Map calculate(DocHV doc, Map data, DocDefIV defIV, NkCardFormDef d, boolean isTrigger, String options) {
        return execSpEL(doc, data, defIV, d, false)
    }

    private Map execSpEL(DocHV doc, Map data, DocDefIV defIV, NkCardFormDef d, isNewCreate){

        EvaluationContext context = spELManager.createContext(doc)

        d.getItems()
                .stream()
                .sorted({ o1, o2 -> (o1.calcOrder <=> o2.calcOrder) })
                .peek({item ->
                    if(StringUtils.isNotBlank(item.spELContent)){

                        def trigger = null
                        if(ArrayUtils.contains(item.spELTriggers,'ALWAYS')){
                            trigger = 'ALWAYS'
                        }else if(ArrayUtils.contains(item.spELTriggers,'INIT') && isNewCreate){
                            trigger = 'INIT'
                        }else if(ArrayUtils.contains(item.spELTriggers,'BLANK') && isBlank(data.get(item.key))){
                            trigger = 'BLANK'
                        }

                        if(trigger!=null){

                            if(log.isInfoEnabled())
                                log.info("{}{} 执行表达式 KEY={} T={} EL={}",
                                        NkDocEngineContext.currLog(),
                                        defIV.getCardKey(),
                                        item.key,
                                        trigger,
                                        item.spELContent
                                )

                            try{
                                data.put(item.key,spELManager.invoke(item.spELContent,context))
                            }catch(Exception e){
                                throw new TfmsDefineException(
                                        String.format("KEY=%s T=%s %s",
                                                item.key,
                                                trigger,
                                                e.getMessage()
                                        )
                                )
                            }
                        }
                    }

                    context.setVariable(item.key, data.get(item.key))
                })
                .forEach({ item ->

                    if(StringUtils.isNotBlank(item.spELControl)){

                        if(log.isInfoEnabled())
                            log.info("{}{} 执行表达式 KEY={} T=CONTROL EL={}",
                                    NkDocEngineContext.currLog(),
                                    defIV.getCardKey(),
                                    item.key,
                                    item.spELControl
                            )
                        try{
                            item.control = spELManager.invoke(item.spELControl,context) as Integer
                        }catch(Exception e){
                            throw new TfmsDefineException(
                                    String.format("KEY=%s T=CONTROL %s",
                                            item.key,
                                            e.getMessage()
                                    )
                            )
                        }
                    }

                    if(StringUtils.isNotBlank(item.options)){
                        try {
                            item.optionsObject = JSON.parse(spELManager.convert(context, item.options))
                        }catch(Exception e){
                            throw new TfmsDefineException(
                                    String.format("KEY=%s T=OPTIONS %s",
                                            item.key,
                                            e.getMessage()
                                    )
                            )
                        }
                    }

                })
        return data
    }

    private static boolean isBlank(Object value){
        return value == null ||
                (value instanceof Array && ArrayUtils.isEmpty(value)) ||
                (value instanceof Collection && CollectionUtils.isEmpty(value)) ||
                StringUtils.isBlank(value.toString())
    }

    static class NkCardFormDef {
        private int col
        private List<NkCardFormDefI> items

        int getCol() {
            return col
        }

        void setCol(int col) {
            this.col = col
        }

        List<NkCardFormDefI> getItems() {
            return items
        }

        void setItems(List<NkCardFormDefI> items) {
            this.items = items
        }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    static class NkCardFormDefI {
        private String key
        private String name
        private String inputType
        private String calcTrigger
        private Integer calcOrder
        private Integer col
        private Boolean required
        private Integer control
        private String spELControl
        private String spELContent
        private String[] spELTriggers
        private String format
        private String options
        private Object optionsObject
        private Float min
        private Float max
        private Integer maxLength
        private Integer digits
        private Float step
        private String selectMode
        private String modal
        private String pattern
        private String message


        String getKey() {
            return key
        }

        void setKey(String key) {
            this.key = key
        }

        String getName() {
            return name
        }

        void setName(String name) {
            this.name = name
        }

        String getInputType() {
            return inputType
        }

        void setInputType(String inputType) {
            this.inputType = inputType
        }

        String getCalcTrigger() {
            return calcTrigger
        }

        void setCalcTrigger(String calcTrigger) {
            this.calcTrigger = calcTrigger
        }

        Integer getCalcOrder() {
            return calcOrder
        }

        void setCalcOrder(Integer calcOrder) {
            this.calcOrder = calcOrder
        }

        Integer getCol() {
            return col
        }

        void setCol(Integer col) {
            this.col = col
        }

        Boolean getRequired() {
            return required
        }

        void setRequired(Boolean required) {
            this.required = required
        }

        String getSpELContent() {
            return spELContent
        }

        void setSpELContent(String spELContent) {
            this.spELContent = spELContent
        }

        String[] getSpELTriggers() {
            return spELTriggers
        }

        void setSpELTriggers(String[] spELTriggers) {
            this.spELTriggers = spELTriggers
        }

        String getFormat() {
            return format
        }

        void setFormat(String format) {
            this.format = format
        }

        String getOptions() {
            return options
        }

        void setOptions(String options) {
            this.options = options
        }

        Object getOptionsObject() {
            return optionsObject
        }

        void setOptionsObject(Object optionsObject) {
            this.optionsObject = optionsObject
        }

        String getModal() {
            return modal
        }

        void setModal(String modal) {
            this.modal = modal
        }

        Float getMin() {
            return min
        }

        void setMin(Float min) {
            this.min = min
        }

        Float getMax() {
            return max
        }

        void setMax(Float max) {
            this.max = max
        }

        Integer getMaxLength() {
            return maxLength
        }

        void setMaxLength(Integer maxLength) {
            this.maxLength = maxLength
        }

        Integer getDigits() {
            return digits
        }

        void setDigits(Integer digits) {
            this.digits = digits
        }

        Float getStep() {
            return step
        }

        void setStep(Float step) {
            this.step = step
        }

        String getSelectMode() {
            return selectMode
        }

        void setSelectMode(String selectMode) {
            this.selectMode = selectMode
        }

        String getPattern() {
            return pattern
        }

        void setPattern(String pattern) {
            this.pattern = pattern
        }

        String getMessage() {
            return message
        }

        void setMessage(String message) {
            this.message = message
        }

        Integer getControl() {
            return control
        }

        void setControl(Integer control) {
            this.control = control
        }

        String getSpELControl() {
            return spELControl
        }

        void setSpELControl(String spELControl) {
            this.spELControl = spELControl
        }
    }
}
