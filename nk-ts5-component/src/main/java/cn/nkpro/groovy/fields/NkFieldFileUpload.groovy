package cn.nkpro.groovy.fields

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractField
import cn.nkpro.ts5.docengine.cards.NkDynamicFormField
import cn.nkpro.ts5.docengine.cards.NkDynamicGridField
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(90)
@NkNote("文件")
@Component("NkFieldFileUpload")
class NkFieldFileUpload extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField {

}
