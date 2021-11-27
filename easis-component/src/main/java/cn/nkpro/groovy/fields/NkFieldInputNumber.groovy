package cn.nkpro.groovy.fields

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractField
import cn.nkpro.easis.docengine.cards.NkCalculateContext
import cn.nkpro.easis.docengine.cards.NkDynamicFormDefI
import cn.nkpro.easis.docengine.cards.NkDynamicFormField
import cn.nkpro.easis.docengine.cards.NkDynamicGridField
import cn.nkpro.easis.docengine.cards.NkLinkageFormField
import cn.nkpro.easis.co.easy.EasySingle
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

import java.math.RoundingMode

@Order(20)
@NkNote("数字")
@Component("NkFieldInputNumber")
class NkFieldInputNumber extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField, NkLinkageFormField {

    @Override
    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext) {

        def value = card.get(field.getKey())

        if(value !=null ){
            card.set(field.getKey(),
                new BigDecimal(value as double)
                    .setScale(field.getInputOptions().getOrDefault('digits', 2) as int, RoundingMode.HALF_UP)
                    .doubleValue()
            )
        }

        super.afterCalculate(field, context, card, calculateContext)
    }
}
