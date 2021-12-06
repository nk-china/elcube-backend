/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.components.defaults.fields

import cn.nkpro.elcard.annotation.NkNote
import cn.nkpro.elcard.docengine.NkAbstractField
import cn.nkpro.elcard.docengine.cards.NkDynamicFormField
import cn.nkpro.elcard.docengine.cards.NkDynamicGridField
import cn.nkpro.elcard.docengine.cards.NkLinkageFormField
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(30)
@NkNote("日期")
@Component("NkFieldDatePicker")
class NkFieldDatePicker extends NkAbstractField implements NkDynamicFormField, NkDynamicGridField, NkLinkageFormField {

}
