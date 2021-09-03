package cn.nkpro.groovy.cards.sidebar

import cn.nkpro.ts5.docengine.abstracts.NkAbstractCard
import cn.nkpro.ts5.annotation.NkNote
import org.springframework.stereotype.Component

@NkNote("单据信息")
@Component("NkCardSidebarDocInfo")
class NkCardSidebarDocInfo extends NkAbstractCard<Map,Map> {

    @Override
    String getPosition() {
        return POSITION_SIDEBAR
    }
}
