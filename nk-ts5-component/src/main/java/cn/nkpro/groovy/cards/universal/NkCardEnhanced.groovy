package cn.nkpro.groovy.cards.universal

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractCard
import cn.nkpro.ts5.docengine.model.DocDefHV
import cn.nkpro.ts5.docengine.model.DocDefIV
import cn.nkpro.ts5.docengine.service.NkDocDefService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("增强原型")
@Component("NkCardEnhanced")
class NkCardEnhanced extends NkAbstractCard<Map<String,String>,Map<String,String>> {

    @Autowired
    private NkDocDefService docDefService

    @Override
    Map<String, String> afterGetDef(DocDefHV defHV, DocDefIV defIV, Map<String, String> stringStringMap) {

        return super.afterGetDef(defHV, defIV, stringStringMap)
    }
}
