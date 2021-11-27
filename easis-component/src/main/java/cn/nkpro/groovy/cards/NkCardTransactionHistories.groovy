package cn.nkpro.groovy.cards

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import cn.nkpro.easis.docengine.NkDocEngine
import cn.nkpro.easis.docengine.NkDocSearchService
import cn.nkpro.easis.docengine.model.DocDefIV
import cn.nkpro.easis.docengine.model.DocHV
import cn.nkpro.easis.docengine.model.SearchParams
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
