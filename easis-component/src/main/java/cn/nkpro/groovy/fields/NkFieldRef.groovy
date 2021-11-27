package cn.nkpro.groovy.fields

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.co.spel.NkSpELManager
import cn.nkpro.easis.docengine.NkAbstractField
import cn.nkpro.easis.docengine.NkDocEngine
import cn.nkpro.easis.docengine.cards.NkBaseContext
import cn.nkpro.easis.docengine.cards.NkCalculateContext
import cn.nkpro.easis.docengine.cards.NkDynamicFormDefI
import cn.nkpro.easis.docengine.cards.NkDynamicFormField
import cn.nkpro.easis.docengine.cards.NkDynamicGridField
import cn.nkpro.easis.docengine.cards.NkLinkageFormField
import cn.nkpro.easis.co.easy.EasySingle
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

import static com.alibaba.fastjson.JSON.parseObject

@Order(80)
@NkNote("单据引用")
@Component("NkFieldRef")
class NkFieldRef extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField, NkLinkageFormField {

    @Autowired
    private NkSpELManager spELManager
    @Autowired
    private NkDocEngine docEngine

    @Override
    void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkBaseContext baseContext) {

        def options = field.getInputOptions().get("options")

        if(options){
            field.getInputOptions().put(
                    "optionsObject",
                    parseObject(spELManager.convert(options as String, context))
            )
        }

        Map data = card.get(field.getKey())

        if(data!=null && data.containsKey("optionMappings")){
            (data.get("optionMappings") as Map).forEach({ k, v ->
                NkDynamicFormDefI f = baseContext.getFields().find { f -> f.getKey() == k }
                if(f){
                    Map map = f.getInputOptions()
                    (v as Map).forEach({vk,vv -> map.put(vk,vv)})
                }
            })
        }
    }

    @Override
    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext) {

        // 当上下文计算由当前字段触发、或者当前字段的值在计算阶段发生改变
        if(calculateContext.fieldTrigger || !Objects.equals(card.get(field.getKey()),calculateContext.original.get(field.getKey()))){
            String optionMappings = field.getInputOptions().get("optionMappings")
            String dataMappings = field.getInputOptions().get("dataMappings")

            Map data = card.get(field.getKey())

            if(StringUtils.isNotBlank(data.get("docId") as CharSequence) && !StringUtils.isAllBlank(optionMappings,dataMappings)){

                // 获取选中的单据
                def refDoc = docEngine.detail(data.get("docId") as String)

                // 如果映射模版不为空
                if(StringUtils.isNotBlank(optionMappings)){
                    def jSONObject = parseObject(spELManager.convert(optionMappings, refDoc))
                    data.put("optionMappings", jSONObject)

                    jSONObject.forEach({ k, v ->
                            NkDynamicFormDefI f = calculateContext.getFields().find { f -> f.getKey() == k }
                            if(f){
                                Map map = f.getInputOptions()
                                (v as Map).forEach({vk,vv -> map.put(vk,vv)})
                            }
                        })
                }

                // 如果映射模版不为空
                if(StringUtils.isNotBlank(dataMappings)){
                    def jSONObject = parseObject(spELManager.convert(dataMappings, refDoc))
                    data.put("dataMappings", jSONObject)
                    // 将数据映射到 卡片数据，这里设置的值，应该符合optionMappings中的规则，不然会导致数据不合理
                    jSONObject.forEach({ k, v ->
                            card.set(k,v)
                            context.setVariable(k,v)
                            calculateContext.getSkip().add(k)
                        })
                }
            }
        }
    }
}
