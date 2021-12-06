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
import cn.nkpro.elcard.docengine.NkDocEngine
import cn.nkpro.elcard.docengine.NkDocSearchService
import cn.nkpro.elcard.docengine.model.DocDefIV
import cn.nkpro.elcard.docengine.model.DocHV
import cn.nkpro.elcard.docengine.model.SearchParams
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("交易历史")
@Component("NkCardTransactionHistories")
class NkCardTransactionHistories extends NkAbstractCard<Map,Map> {

    @Autowired
    NkDocEngine docEngine

    @Autowired
    NkDocSearchService docSearchService

    @Override
    Object call(DocHV doc, Map data, DocDefIV defIV, Map d, Object options) {
        return docSearchService.queryList(
                QueryBuilders.termQuery("refObjectId", doc.getRefObjectId()),
                SearchParams.defaults(1000)
                        .order("createdTime", true)
        )
    }
}
