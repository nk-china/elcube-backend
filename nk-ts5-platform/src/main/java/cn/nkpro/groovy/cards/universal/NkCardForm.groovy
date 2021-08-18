package cn.nkpro.groovy.cards.universal

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.engine.doc.abstracts.NkAbstractCard
import com.alibaba.fastjson.JSONObject
import org.springframework.stereotype.Component

@WsDocNote("基础表单")
@Component("NkCardForm")
class NkCardForm extends NkAbstractCard<JSONObject,NkCardFormDef> {



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

    static class NkCardFormDefI {
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
        private String conditions
        private Float min
        private Float max
        private Integer maxLength

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

        String getConditions() {
            return conditions
        }

        void setConditions(String conditions) {
            this.conditions = conditions
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
    }
}
