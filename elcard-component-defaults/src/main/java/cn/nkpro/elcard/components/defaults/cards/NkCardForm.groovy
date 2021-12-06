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
package cn.nkpro.elcard.components.defaults.cards

import cn.nkpro.elcard.annotation.NkNote
import cn.nkpro.elcard.docengine.NkAbstractCard
import cn.nkpro.elcard.docengine.model.DocDefIV
import cn.nkpro.elcard.docengine.model.DocHV
import cn.nkpro.elcard.docengine.model.NkCardFormDefI
import cn.nkpro.elcard.docengine.utils.NkFormCardHelper
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Deprecated
@SuppressWarnings("unused")
@NkNote("基础表单")
@Component("NkCardForm")
class NkCardForm extends NkAbstractCard<Map,NkCardFormDef> {

    @Autowired
    private NkFormCardHelper nkFormCardHelper

    @Override
    Map afterCreate(DocHV doc, DocHV preDoc, Map data, DocDefIV defIV, NkCardFormDef d) {
        return nkFormCardHelper.execSpEL(doc, data, defIV, d.getItems(), true, true)
    }

    @Override
    Map afterGetData(DocHV doc, Map data, DocDefIV defIV, NkCardFormDef d) {
        return nkFormCardHelper.execSpEL(doc, data, defIV, d.getItems(), false, true)
    }

    @Override
    Map calculate(DocHV doc, Map data, DocDefIV defIV, NkCardFormDef d, boolean isTrigger, Object options) {
        return nkFormCardHelper.execSpEL(doc, data, defIV, d.getItems(), false, true)
    }

    @Override
    Map random(DocHV docHV, DocDefIV defIV, NkCardFormDef d) {
        return nkFormCardHelper.random(d.getItems())
    }



    @JsonIgnoreProperties(ignoreUnknown=true)
    static class NkCardFormDef {
        private int col
        private List<NkCardFormDefI> items = new ArrayList<>()

        int getCol() {
            return col
        }

        void setCol(int col) {
            this.col = col
        }

        List<NkCardFormDefI> getItems() {
            return items
        }

        void setItems(List<NkCardFormDefI> items) {
            this.items = items
            if(this.items == null){
                this.items = new ArrayList<>()
            }
        }
    }
}
