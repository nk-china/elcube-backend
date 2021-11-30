package cn.nkpro.easis.components.defaults.fields

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractField
import cn.nkpro.easis.docengine.cards.NkDynamicFormField
import cn.nkpro.easis.docengine.cards.NkDynamicGridField
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(90)
@NkNote("文件")
@Component("NkFieldFileUpload")
class NkFieldFileUpload extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField {

}
