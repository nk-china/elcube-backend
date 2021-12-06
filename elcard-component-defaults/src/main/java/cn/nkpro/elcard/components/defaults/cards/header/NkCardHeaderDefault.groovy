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
package cn.nkpro.elcard.components.defaults.cards.header

import cn.nkpro.elcard.annotation.NkNote
import cn.nkpro.elcard.co.spel.NkSpELManager
import cn.nkpro.elcard.docengine.NkAbstractCard
import cn.nkpro.elcard.docengine.model.DocDefIV
import cn.nkpro.elcard.docengine.model.DocHV
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


@Order(10)
@NkNote("交易抬头")
@Component("NkCardHeaderDefault")
class NkCardHeaderDefault extends NkAbstractCard<Map,Def> {

    @Autowired
    private NkSpELManager spELManager

    @Override
    String getPosition() {
        return POSITION_HEADER
    }

    @Override
    Map afterGetData(DocHV doc, Map data, DocDefIV defIV, Def d) {

        if(StringUtils.isNotBlank(d.getPartnerNameSpEL())){
            if(StringUtils.isBlank(doc.getPartnerName())){
                def context = spELManager.createContext(doc)
                doc.setPartnerName(spELManager.invoke(d.getPartnerNameSpEL(), context) as String)
            }
        }

        return super.afterGetData(doc, data, defIV, d) as Map
    }

    @Override
    Map afterCreate(DocHV doc, DocHV preDoc, Map data, DocDefIV defIV, Def d) {

        def context = spELManager.createContext(doc)

        if(StringUtils.isNotBlank(d.getPartnerIdSpEL())){
            doc.setPartnerId(spELManager.invoke(d.getPartnerIdSpEL(), context) as String)
        }

        if(StringUtils.isNotBlank(d.getPartnerNameSpEL())){
            doc.setPartnerName(spELManager.invoke(d.getPartnerNameSpEL(), context) as String)
        }
        return super.afterCreate(doc, preDoc, data, defIV, d) as Map
    }

    @Override
    Map beforeUpdate(DocHV doc, Map data, Map original, DocDefIV defIV, Def d) {

        def context = spELManager.createContext(doc)

        if(StringUtils.isNotBlank(d.getPartnerIdSpEL())){
            doc.setPartnerId(spELManager.invoke(d.getPartnerIdSpEL(), context) as String)
        }

        if(StringUtils.isNotBlank(d.getPartnerNameSpEL())){
            doc.setPartnerName(spELManager.invoke(d.getPartnerNameSpEL(), context) as String)
        }

        return data
    }

    static class Def{
        String partnerIdSpEL
        String partnerNameSpEL
    }
}
