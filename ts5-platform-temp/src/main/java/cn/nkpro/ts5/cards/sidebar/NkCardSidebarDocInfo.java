package cn.nkpro.ts5.cards.sidebar;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.NKAbstractCard;
import cn.nkpro.ts5.engine.doc.NKCard;
import org.springframework.stereotype.Component;

import java.util.Map;

@WsDocNote("单据信息")
@Component("NkCardSidebarDocInfo")
public class NkCardSidebarDocInfo extends NKAbstractCard<Map,Map> {

    @Override
    public String getPosition() {
        return NKCard.POSITION_SIDEBAR;
    }
}
