package cn.nkpro.groovy.cards.header

import cn.nkpro.ts5.docengine.abstracts.NkAbstractCard
import cn.nkpro.ts5.wsdoc.annotation.WsDocNote
import org.springframework.stereotype.Component

@WsDocNote("交易伙伴抬头")
@Component("NkCardHeaderPartner")
class NkCardHeaderPartner extends NkAbstractCard<Map,Map> {

    @Override
    String getPosition() {
        return POSITION_HEADER
    }
}
