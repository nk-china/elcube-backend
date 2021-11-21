package cn.nkpro.groovy.fields

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.co.spel.NkSpELManager
import cn.nkpro.ts5.docengine.NkAbstractField
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component

@NkNote("级联选择")
@Component("NkFieldCascader")
class NkFieldCascader extends NkAbstractField {

    @Autowired
    private NkSpELManager spELManager

    @Override
    Object process(Object value, Map<String, Object> inputOptions, EvaluationContext context) {

        def options = inputOptions.get("options")

        if(options){

            JSONArray array = JSON.parseArray(spELManager.convert(options as String, context))

            inputOptions.put( "optionsObject",array)

            // todo 递归判断值是否合法
//            def a = array.stream()
//                    .find {i-> Objects.equals(value, i["value"])}
//
//            if(!a){
//                value = null
//            }
        }

        return super.process(value, inputOptions, context)
    }
}
