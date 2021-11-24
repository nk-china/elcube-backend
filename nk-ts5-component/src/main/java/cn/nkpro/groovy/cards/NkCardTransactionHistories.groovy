package cn.nkpro.groovy.cards

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractCard
import cn.nkpro.ts5.docengine.NkDocEngine
import cn.nkpro.ts5.docengine.NkDocSearchService
import cn.nkpro.ts5.docengine.model.DocDefIV
import cn.nkpro.ts5.docengine.model.DocHV
import cn.nkpro.ts5.docengine.model.SearchParams
import org.elasticsearch.index.query.QueryBuilder
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
        )
    }
}
