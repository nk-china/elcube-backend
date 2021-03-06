/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.components.defaults.cards.header

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.co.spel.NkSpELManager
import cn.nkpro.elcube.docengine.NkAbstractCard
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
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

        /*
         * 如果单据交易伙伴ID不为空，单据引擎会自动查找单据最新的单据名称
         *
         * 但是如果单据交易伙伴名称SpEL 不为空，以用户配置的表达式结果为准，所以这里会再赋值一次
         */
        if(StringUtils.isNotBlank(d.getPartnerNameSpEL())){
            def context = spELManager.createContext(doc)
            doc.setPartnerName(spELManager.invoke(d.getPartnerNameSpEL(), context) as String)
        }

        return super.afterGetData(doc, data, defIV, d) as Map
    }

    @Override
    Map afterCreate(DocHV doc, DocHV preDoc, Map data, DocDefIV defIV, Def d) {
        apply(doc,d)
        return super.afterCreate(doc, preDoc, data, defIV, d) as Map
    }

    @Override
    Map calculate(DocHV doc, Map data, DocDefIV defIV, Def d, boolean isTrigger, Object options) {
        apply(doc,d)
        return super.calculate(doc, data, defIV, d, isTrigger, options) as Map
    }

    @Override
    Map beforeUpdate(DocHV doc, Map data, Map original, DocDefIV defIV, Def d) {
        apply(doc,d)
        return data
    }

    void apply(DocHV doc, Def d){

        def context = spELManager.createContext(doc)

        /*
         * 如果交易伙伴ID SpEL不为空，设置单据的交易伙伴ID
         *
         */
        if(StringUtils.isNotBlank(d.getPartnerIdSpEL())){
            doc.setPartnerId(spELManager.invoke(d.getPartnerIdSpEL(), context) as String)
        }
        /*
         * 如果单据交易伙伴名称SpEL 不为空，以用户配置的表达式结果为准
         * 否则，单据引擎会自动查找单据最新的单据名称
         */
        if(StringUtils.isNotBlank(d.getPartnerNameSpEL())){
            doc.setPartnerName(spELManager.invoke(d.getPartnerNameSpEL(), context) as String)
        }

        if(StringUtils.isNotBlank(d.docNameSpEL)){
            doc.setDocName(spELManager.invoke(d.docNameSpEL, context) as String)
        }else if(!doc.docName){
            if(StringUtils.isNotBlank(d.docNameDefaultSpEL)){
                doc.docName = spELManager.invoke(d.docNameDefaultSpEL, context) as String
            }else{
                doc.docName = doc.def.docName
            }
        }
    }

    static class Def{
        String partnerIdSpEL
        String partnerNameSpEL
        String docNameDefaultSpEL
        String docNameSpEL
    }
}
