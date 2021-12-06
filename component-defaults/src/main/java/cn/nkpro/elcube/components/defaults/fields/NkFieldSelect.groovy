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
import cn.nkpro.elcube.co.spel.NkSpELManager
import cn.nkpro.elcube.docengine.NkAbstractField
import cn.nkpro.elcube.docengine.cards.NkBaseContext
import cn.nkpro.elcube.docengine.cards.NkCalculateContext
import cn.nkpro.elcube.docengine.cards.NkDynamicFormDefI
import cn.nkpro.elcube.docengine.cards.NkDynamicFormField
import cn.nkpro.elcube.docengine.cards.NkDynamicGridField
import cn.nkpro.elcube.docengine.cards.NkLinkageFormField
import cn.nkpro.elcube.co.easy.EasySingle
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
