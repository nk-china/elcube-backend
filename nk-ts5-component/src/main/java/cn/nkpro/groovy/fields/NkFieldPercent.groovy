package cn.nkpro.groovy.fields

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractField
import cn.nkpro.ts5.docengine.cards.NkCalculateContext
import cn.nkpro.ts5.docengine.cards.NkDynamicFormDefI
import cn.nkpro.ts5.docengine.cards.NkDynamicFormField
import cn.nkpro.ts5.docengine.cards.NkDynamicGridField
import cn.nkpro.ts5.docengine.cards.NkLinkageFormField
import cn.nkpro.ts5.co.easy.EasySingle
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
