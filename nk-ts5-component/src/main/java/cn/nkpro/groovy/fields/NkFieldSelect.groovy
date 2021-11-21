package cn.nkpro.groovy.fields

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.co.spel.NkSpELManager
import cn.nkpro.ts5.docengine.NkAbstractField
import com.alibaba.fastjson.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("下拉选择框")
@Component("NkFieldSelect")
class NkFieldSelect extends NkAbstractField {

    @Autowired
    private NkSpELManager spELManager

    @Override
    void afterGetDef(Map<String, Object> inputOptions) {

        def options = inputOptions.get("options")

        if(options){
            inputOptions.put(
                "options",
                    JSON.parse(spELManager.convert(options as String, null))
            )
        }

        super.afterGetDef(inputOptions)
    }
}
