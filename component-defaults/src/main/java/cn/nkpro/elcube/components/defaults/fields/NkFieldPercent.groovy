/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.components.defaults.fields

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.docengine.NkAbstractField
import cn.nkpro.elcube.docengine.cards.NkCalculateContext
import cn.nkpro.elcube.docengine.cards.NkDynamicFormDefI
import cn.nkpro.elcube.docengine.cards.NkDynamicFormField
import cn.nkpro.elcube.docengine.cards.NkDynamicGridField
import cn.nkpro.elcube.docengine.cards.NkLinkageFormField
import cn.nkpro.elcube.co.easy.EasySingle
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
                    .setScale(field.getInputOptions().getOrDefault('digits', 6) as int, RoundingMode.HALF_UP)
                    .doubleValue()
            )
        }

        super.afterCalculate(field, context, card, calculateContext)
    }
}
