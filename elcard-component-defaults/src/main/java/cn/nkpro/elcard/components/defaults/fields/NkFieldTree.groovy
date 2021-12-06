/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.components.defaults.fields

import cn.nkpro.elcard.annotation.NkNote
import cn.nkpro.elcard.co.spel.NkSpELManager
import cn.nkpro.elcard.docengine.NkAbstractField
import cn.nkpro.elcard.docengine.cards.NkBaseContext
import cn.nkpro.elcard.docengine.cards.NkDynamicFormDefI
import cn.nkpro.elcard.docengine.cards.NkDynamicFormField
import cn.nkpro.elcard.co.easy.EasySingle
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

@Order(60)
@NkNote("树")
@Component("NkFieldTree")
class NkFieldTree extends NkAbstractField implements NkDynamicFormField {

    @Autowired
    private NkSpELManager spELManager

    @Override
    void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkBaseContext baseContext) {

        def options = field.getInputOptions().get("options")

        if(options){

            JSONArray array = JSON.parseArray(spELManager.convert(options as String, context))

            field.getInputOptions().put( "optionsObject",array)

            // todo 递归判断值是否合法
//            def a = array.stream()
//                    .find {i-> Objects.equals(value, i["value"])}
//
//            if(!a){
//                value = null
//            }
        }
    }
}
