package cn.nkpro.groovy.fields;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.docengine.NkAbstractField
import cn.nkpro.easis.docengine.cards.NkDynamicFormField
import cn.nkpro.easis.docengine.cards.NkDynamicGridField
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(10)
@NkNote("文本")
@Component("NkFieldInput")
class NkFieldInput extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField {

}
