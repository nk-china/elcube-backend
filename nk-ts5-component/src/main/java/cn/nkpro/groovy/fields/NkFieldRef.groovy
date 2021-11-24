package cn.nkpro.groovy.fields

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.co.spel.NkSpELManager
import cn.nkpro.ts5.docengine.NkAbstractField
import cn.nkpro.ts5.docengine.NkDocEngine
import cn.nkpro.ts5.docengine.cards.NkDynamicCalculateContext
import cn.nkpro.ts5.docengine.cards.NkDynamicFormDefI
import cn.nkpro.ts5.docengine.cards.NkDynamicFormField
import cn.nkpro.ts5.docengine.cards.NkDynamicGridField
import cn.nkpro.ts5.docengine.model.easy.EasySingle
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

import static com.alibaba.fastjson.JSON.parseObject

@Order(80)
@NkNote("单据引用")
@Component("NkFieldRef")
class NkFieldRef extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField {

    @Autowired
    private NkSpELManager spELManager
    @Autowired
    private NkDocEngine docEngine

    @Override
    void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkDynamicCalculateContext calculateContext) {

        def options = field.getInputOptions().get("options")

        if(options){
            field.getInputOptions().put(
                    "optionsObject",
                    parseObject(spELManager.convert(options as String, context))
            )
        }
    }

    @Override
    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkDynamicCalculateContext calculateContext) {

        String dataMappings = field.getInputOptions().get("dataMappings")

        // 如果映射模版不为空
        if(StringUtils.isNotBlank(dataMappings)){

            Map data = card.get(field.getKey())

            // 当上下文计算由当前字段触发、或者当前字段的值在计算阶段发生改变
            if(calculateContext.fieldTrigger || !Objects.equals(card.get(field.getKey()),calculateContext.original.get(field.getKey()))){

                // 获取选中的单据
                def refDoc = docEngine.detail(data.get("docId") as String)

                // 将数据映射到 卡片数据
                def json = parseObject(spELManager.convert(dataMappings, refDoc))

                json.forEach({ k, v -> card.set(k,v) })
            }
        }

    }
}
