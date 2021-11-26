package cn.nkpro.groovy.cards.header

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractCard
import cn.nkpro.ts5.docengine.model.DocDefIV
import cn.nkpro.ts5.docengine.model.DocHV
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(11)
@NkNote("交易伙伴抬头")
@Component("NkCardHeaderPartner")
class NkCardHeaderPartner extends NkAbstractCard<Map,Map> {

    @Override
    String getPosition() {
        return POSITION_HEADER
    }

    @Override
    Map afterGetData(DocHV doc, Map data, DocDefIV defIV, Map d) {

        doc.setPartnerName(doc.getDocName())

        return super.afterGetData(doc, data, defIV, d) as Map
    }
}
