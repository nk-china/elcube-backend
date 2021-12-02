/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.components.defaults.cards

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import cn.nkpro.easis.docengine.model.DocDefHV
import cn.nkpro.easis.docengine.model.DocHV
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
