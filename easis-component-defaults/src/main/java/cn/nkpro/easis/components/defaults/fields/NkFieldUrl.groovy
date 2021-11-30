package cn.nkpro.easis.components.defaults.fields

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractField
import cn.nkpro.easis.docengine.cards.NkDynamicFormField
import cn.nkpro.easis.docengine.cards.NkDynamicGridField
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(11)
@NkNote("URL")
@Component("NkFieldUrl")
class NkFieldUrl extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField {

}
