package cn.nkpro.groovy.cards.header

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.co.spel.NkSpELManager
import cn.nkpro.ts5.docengine.NkAbstractCard
import cn.nkpro.ts5.docengine.model.DocDefIV
import cn.nkpro.ts5.docengine.model.DocHV
import lombok.Data
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.expression.EvaluationContext
import org.springframework.stereotype.Component


@Order(10)
@NkNote("交易抬头")
@Component("NkCardHeaderDefault")
class NkCardHeaderDefault extends NkAbstractCard<Map,Def> {

    @Autowired
    private NkSpELManager spELManager

    @Override
    String getPosition() {
        return POSITION_HEADER
    }

    @Override
    Map beforeUpdate(DocHV doc, Map data, Map original, DocDefIV defIV, Def d) {

        def context = spELManager.createContext(doc)

        if(StringUtils.isNotBlank(d.getPartnerIdSpEL())){
            doc.setPartnerId(spELManager.invoke(d.getPartnerIdSpEL(), context) as String)
        }

        if(StringUtils.isNotBlank(d.getPartnerNameSpEL())){
            doc.setPartnerName(spELManager.invoke(d.getPartnerNameSpEL(), context) as String)
        }

        return data
    }

    static class Def{
        String partnerIdSpEL
        String partnerNameSpEL
    }
}
