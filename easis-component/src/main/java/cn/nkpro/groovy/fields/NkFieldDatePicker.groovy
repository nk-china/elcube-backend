package cn.nkpro.groovy.fields

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractField
import cn.nkpro.easis.docengine.cards.NkDynamicFormField
import cn.nkpro.easis.docengine.cards.NkDynamicGridField
import cn.nkpro.easis.docengine.cards.NkLinkageFormField
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(30)
@NkNote("日期")
@Component("NkFieldDatePicker")
class NkFieldDatePicker extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField, NkLinkageFormField {

}
