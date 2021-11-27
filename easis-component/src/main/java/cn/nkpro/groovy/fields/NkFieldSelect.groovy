package cn.nkpro.groovy.fields

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.co.spel.NkSpELManager
import cn.nkpro.easis.docengine.NkAbstractField
import cn.nkpro.easis.docengine.cards.NkBaseContext
import cn.nkpro.easis.docengine.cards.NkCalculateContext
import cn.nkpro.easis.docengine.cards.NkDynamicFormDefI
import cn.nkpro.easis.docengine.cards.NkDynamicFormField
import cn.nkpro.easis.docengine.cards.NkDynamicGridField
import cn.nkpro.easis.docengine.cards.NkLinkageFormField
import cn.nkpro.easis.co.easy.EasySingle
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

@Order(40)
@NkNote("选择")
@Component("NkFieldSelect")
class NkFieldSelect extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField, NkLinkageFormField {

    @Autowired
    private NkSpELManager spELManager

    @Override
    void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkBaseContext baseContext) {

        def options = field.getInputOptions().get("options")

        if(options){
            JSONArray array = JSON.parseArray(spELManager.convert(options as String, context))

            field.getInputOptions().put( "optionsObject",array)
        }
    }

    @Override
    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext) {

        JSONArray array = field.getInputOptions().get("optionsObject") as JSONArray
        if(!array){
            card.set(field.getKey(), null)
        }else{
            def value = card.get(field.getKey())
            def a = array.stream()
                    .find {i-> Objects.equals(value, i["value"])}
            if(!a){
                card.set(field.getKey(), null)
            }
        }
    }
}
