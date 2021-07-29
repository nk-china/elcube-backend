package cn.nkpro.ts5.cards.header

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.engine.doc.NKAbstractCard
import org.springframework.stereotype.Component


@WsDocNote("交易抬头")
@Component("NkCardHeaderDefault")
class NkCardHeaderDefault extends NKAbstractCard<Map,Map> {

    @Override
    String getPosition() {
        return POSITION_HEADER
    }

    @Override
    String[] getDefComponentNames() {
        return ["NkCardHeaderDefaultDef"]
    }
}
