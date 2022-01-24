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
import cn.nkpro.elcube.co.easy.EasySingle
import cn.nkpro.elcube.co.spel.NkSpELManager
import cn.nkpro.elcube.docengine.NkAbstractField
import cn.nkpro.elcube.docengine.cards.NkCalculateContext
import cn.nkpro.elcube.docengine.cards.NkDynamicFormDefI
import cn.nkpro.elcube.docengine.cards.NkDynamicFormField
import cn.nkpro.elcube.docengine.cards.NkDynamicGridField
import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

@Order(10)
@NkNote("文本")
@Component("NkFieldInput")
class NkFieldInput extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField {
    @Autowired
    private NkSpELManager spELManager

    @Override
    void beforeCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext) {
        if (calculateContext.isFieldTrigger() || !Objects.equals(card.get(field.getKey()), calculateContext.original.get(field.getKey()))) {
            String dataMappings = field.getInputOptions().get("dataMappings")
            String method = field.getInputOptions().get("method")

            if(StringUtils.isNotBlank(method)) {
                def jSONObject = JSON.toJSON(spELManager.invoke(method, context))
                if (StringUtils.isNotBlank(dataMappings)) {
                    def result = JSON.parseObject(spELManager.convert(dataMappings, jSONObject))
                    result.forEach({ k, v ->
                        card.set(k, v)
                    })
                }
            }
        }
    }
}
