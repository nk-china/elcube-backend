package cn.nkpro.ts5.cards.header

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.engine.doc.NkAbstractCard
import org.springframework.stereotype.Component

@WsDocNote("交易伙伴抬头")
@Component("NkCardHeaderPartner")
class NkCardHeaderPartner extends NkAbstractCard<Map,Map> {

    @Override
    String getPosition() {
        return POSITION_HEADER
    }
}
