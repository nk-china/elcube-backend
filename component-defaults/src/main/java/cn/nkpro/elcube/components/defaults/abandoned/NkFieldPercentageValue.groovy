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
package cn.nkpro.elcube.components.defaults.abandoned

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.co.spel.NkSpELManager
import cn.nkpro.elcube.docengine.NkAbstractField
import cn.nkpro.elcube.docengine.cards.NkCalculateContext
import cn.nkpro.elcube.docengine.cards.NkDynamicFormDefI
import cn.nkpro.elcube.docengine.cards.NkLinkageFormField
import cn.nkpro.elcube.co.easy.EasySingle
import io.jsonwebtoken.lang.Assert
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext

@Order(20)
@NkNote("百分比金额")
//@Component("NkFieldPercentageValue")
class NkFieldPercentageValue extends NkAbstractField implements NkLinkageFormField {

    @Autowired
    private NkSpELManager spELManager

    @Override
    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext) {

        // 默认通过百分比计算值
        def calcByPercent = true

        // 如果计算由非当前字段触发，判断计算的方向
        if(calculateContext.fieldTrigger){
            def triggerEvent = calculateContext.getOptions().get("triggerEvent")
            Assert.isTrue(triggerEvent instanceof Map,"字段触发事件必须是一个Map")
            Map eventMap = triggerEvent as Map
            calcByPercent = "percent" == eventMap.get("target")
        }


        def baseSpEL = field.getInputOptions().get("baseSpEL")

        // 如果基本值为0，清空值
        if(StringUtils.isBlank(baseSpEL as CharSequence)){
            card.set(field.getKey(), [percent:0,value:0])
            return
        }
        // 计算基本值
        def baseDef = spELManager.invoke(baseSpEL as String, context)
        double base = baseDef? baseDef as double : 0d

        // 初始化字段值
        def fieldValue = card.get(field.getKey())
        def percentDef = fieldValue instanceof Map ? fieldValue.get("percent") : 0d
        def valueDef   = fieldValue instanceof Map ? fieldValue.get("value")   : 0d
        double percent = percentDef ? percentDef as double : 0d
        double value   = valueDef   ? valueDef   as double : 0d

        // 根据触发事件类型进行计算
        if(calcByPercent){
            value = base * percent * 0.01
        }else{
            percent = base == 0 ? 0 : (value / base) * 100
        }
        card.set(field.getKey(), [percent:percent,value:value])
    }
}
