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
import cn.nkpro.elcard.docengine.model.DocDefHV
import cn.nkpro.elcard.docengine.model.DocHV
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@NkNote("日期")
@Component("NkCardDate")
class NkCardDate extends NkAbstractCard<Map,Map> {

    Logger logger = LoggerFactory.getLogger(getClass());

    String date;

    NkCardDate(){
    }
    //@Override
    String getComponentDesc() {
        return null
    }

    //@Override
    protected Map doGetData(DocHV doc, Map o) throws Exception {
        return null
    }

    //@Override
    protected Map toCreate(DocHV doc, DocHV preDoc, DocDefHV docDef, Map o) throws Exception {
        return null
    }

    //@Override
    protected void doUpdate(DocHV doc, DocDefHV docDef, Map data, Map o) throws Exception {

    }


}
