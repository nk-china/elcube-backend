package cn.nkpro.ts5.cards.header;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.NKAbstractCard;
import cn.nkpro.ts5.engine.doc.NKCard;
import org.springframework.stereotype.Component;

import java.util.Map;

@WsDocNote("交易伙伴抬头")
@Component("NkCardHeaderPartner")
public class NkCardHeaderPartner extends NKAbstractCard<Map,Map> {

    @Override
    public String getPosition() {
        return NKCard.POSITION_HEADER;
    }

    @Override
    public String[] getDefComponentNames() {
        return new String[]{"NkCardHeaderPartnerDef"};
    }
}
