package cn.nkpro.ts5.cards.sidebar

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.engine.doc.NKAbstractCard
import org.springframework.stereotype.Component

@WsDocNote("单据信息")
@Component("NkCardSidebarDocInfo")
class NkCardSidebarDocInfo extends NKAbstractCard<Map,Map> {

    @Override
    String getPosition() {
        return POSITION_SIDEBAR
    }
}
