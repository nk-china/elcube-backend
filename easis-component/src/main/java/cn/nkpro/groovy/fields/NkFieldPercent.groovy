package cn.nkpro.groovy.fields

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractField
import cn.nkpro.easis.docengine.cards.NkCalculateContext
import cn.nkpro.easis.docengine.cards.NkDynamicFormDefI
import cn.nkpro.easis.docengine.cards.NkDynamicFormField
import cn.nkpro.easis.docengine.cards.NkDynamicGridField
import cn.nkpro.easis.docengine.cards.NkLinkageFormField
import cn.nkpro.easis.co.easy.EasySingle
import com.alibaba.fastjson.JSONArray
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component
import java.math.RoundingMode

@Order(21)
@NkNote("百分比")
@Component("NkFieldPercent")
class NkFieldPercent extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField, NkLinkageFormField {

    @Override
    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext) {

        def value = card.get(field.getKey())

        // 如果字段被指定来options，先校验value是否合法
        if(field.getInputOptions().containsKey("options")){
            JSONArray array = field.getInputOptions().get("options") as JSONArray
            if(array){
                def find = array.stream().find {i-> value==i}
                if(!find){
                    value = array.size()>0?array[0]:null
                    card.set(field.getKey(), value)
                }
            }
        }
        if(field.getInputOptions().containsKey("min")){
            def min = field.getInputOptions().get("min") as Double
            if(value < min){
                value = min
            }
        }
        if(field.getInputOptions().containsKey("max")){
            def max = field.getInputOptions().get("max") as Double
            if(value > max){
                value = max
            }
        }

        if(value !=null ){
            card.set(field.getKey(),
                new BigDecimal(value as double)
                    .setScale(field.getInputOptions().getOrDefault('digits', 4) as int, RoundingMode.HALF_UP)
                    .doubleValue()
            )
        }

        super.afterCalculate(field, context, card, calculateContext)
    }
}
