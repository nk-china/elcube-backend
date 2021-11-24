package cn.nkpro.groovy.cards.header

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractCard
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


@Order(10)
@NkNote("交易抬头")
@Component("NkCardHeaderDefault")
class NkCardHeaderDefault extends NkAbstractCard<Map,Map> {

    @Override
    String getPosition() {
        return POSITION_HEADER
    }
}
