package cn.nkpro.groovy.cards.universal

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.engine.doc.abstracts.NkAbstractCard
import cn.nkpro.ts5.engine.doc.model.DocDefIV
import cn.nkpro.ts5.engine.doc.model.DocHV
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

@WsDocNote("基础表格")
@Component("NkCardGrid")
class NkCardGrid extends NkAbstractCard<JSONArray,NkCardGridDef> {

    @Override
    JSONArray beforeUpdate(DocHV doc, JSONArray data, JSONArray original, DocDefIV defIV, NkCardGridDef nkCardGridDef) {
        data.forEach({ item ->
            JSONObject json = (JSONObject)item
            json.remove("_XID")
            nkCardGridDef.items
                .forEach({ defItem ->
                    if(StringUtils.equalsIgnoreCase(defItem.getInputType(),"integer")){
                        if(json.containsKey(defItem.key)){
                            json.put(defItem.key,json.getInteger(defItem.key))
                        }
                    }else
                    if(StringUtils.equalsAnyIgnoreCase(defItem.getInputType(),"decimal","percent")){
                        if(json.containsKey(defItem.key)){
                            json.put(defItem.key,json.getFloat(defItem.key))
                        }
                    }
                })
        })
        return data
    }

    static class NkCardGridDef {
        private List<NkCardGridDefI> items

        List<NkCardGridDefI> getItems() {
            return items
        }

        void setItems(List<NkCardGridDefI> items) {
            this.items = items
        }
    }

    static class NkCardGridDefI {
        private String key
        private String name
        private String inputType
        private String calcTrigger
        private Integer calcOrder
        private Integer col
        private Boolean required
        private Boolean readonly
        private String spELContent
        private String[] spELTriggers
        private String eval
        private String format
        private String options
        private Float min
        private Float max
        private Integer maxLength
        private Integer digits
        private Float step
        private String selectMode
        private String checked
        private String unChecked
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

        Boolean getReadonly() {
            return readonly
        }

        void setReadonly(Boolean readonly) {
            this.readonly = readonly
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

        String getEval() {
            return eval
        }

        void setEval(String eval) {
            this.eval = eval
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

        String getChecked() {
            return checked
        }

        void setChecked(String checked) {
            this.checked = checked
        }

        String getUnChecked() {
            return unChecked
        }

        void setUnChecked(String unChecked) {
            this.unChecked = unChecked
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
    }
}
