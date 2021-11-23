package cn.nkpro.groovy.fields

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.co.spel.NkSpELManager
import cn.nkpro.ts5.docengine.NkAbstractField
import cn.nkpro.ts5.docengine.cards.NkDynamicCalculateContext
import cn.nkpro.ts5.docengine.cards.NkDynamicFormDefI
import cn.nkpro.ts5.docengine.model.DocHV
import cn.nkpro.ts5.docengine.model.easy.EasySingle
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

@NkNote("选择")
@Component("NkFieldSelect")
class NkFieldSelect extends NkAbstractField {

    @Autowired
    private NkSpELManager spELManager

    @Override
    void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkDynamicCalculateContext calculateContext) {

        def options = field.getInputOptions().get("options")

        if(options){
            JSONArray array = JSON.parseArray(spELManager.convert(options as String, context))

            field.getInputOptions().put( "optionsObject",array)
        }
    }

    @Override
    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkDynamicCalculateContext calculateContext) {

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
