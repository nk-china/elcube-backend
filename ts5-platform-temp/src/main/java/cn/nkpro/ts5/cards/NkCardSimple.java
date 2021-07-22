package cn.nkpro.ts5.cards;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.NKAbstractCard;
import org.springframework.stereotype.Component;

import java.util.Map;

@WsDocNote("测试")
@Component("NkCardSimple")
public class NkCardSimple extends NKAbstractCard<Map,Map> {
    @Override
    public String[] getDefComponentNames() {
        return new String[]{"NkCardSimpleDef"};
    }
}
