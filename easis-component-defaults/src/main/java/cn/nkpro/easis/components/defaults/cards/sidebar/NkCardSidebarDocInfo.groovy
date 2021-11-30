package cn.nkpro.easis.components.defaults.cards.sidebar

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import org.springframework.stereotype.Component

@NkNote("单据信息")
@Component("NkCardSidebarDocInfo")
class NkCardSidebarDocInfo extends NkAbstractCard<Map,Map> {

    @Override
    String getPosition() {
        return POSITION_SIDEBAR
    }
}
