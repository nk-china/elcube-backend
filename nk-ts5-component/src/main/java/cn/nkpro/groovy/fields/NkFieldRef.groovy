package cn.nkpro.groovy.fields

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.co.spel.NkSpELManager
import cn.nkpro.ts5.docengine.NkAbstractField
import cn.nkpro.ts5.docengine.cards.NkDynamicFormDefI
import cn.nkpro.ts5.docengine.model.DocHV
import cn.nkpro.ts5.docengine.model.easy.EasySingle
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

@NkNote("单据引用")
@Component("NkFieldRef")
class NkFieldRef extends NkAbstractField {

    @Autowired
    private NkSpELManager spELManager

    @Override
    void processOptions(NkDynamicFormDefI field, EvaluationContext context, DocHV doc, EasySingle card) {

        def options = field.getInputOptions().get("options")

        if(options){
            field.getInputOptions().put(
                    "optionsObject",
                    JSON.parseObject(spELManager.convert(options as String, context))
            )
        }
    }

}
