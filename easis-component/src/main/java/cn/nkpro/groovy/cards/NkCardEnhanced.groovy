package cn.nkpro.groovy.cards

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import org.springframework.stereotype.Component

@NkNote("增强原型")
@Component("NkCardEnhanced")
class NkCardEnhanced extends NkAbstractCard<Map<String,String>,Map<String,String>> {
}
