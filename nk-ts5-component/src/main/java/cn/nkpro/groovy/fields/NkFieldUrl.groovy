package cn.nkpro.groovy.fields

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractField
import cn.nkpro.ts5.docengine.cards.NkDynamicFormField
import cn.nkpro.ts5.docengine.cards.NkDynamicGridField
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(11)
@NkNote("URL")
@Component("NkFieldUrl")
class NkFieldUrl extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField {

}
