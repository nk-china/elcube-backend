package cn.nkpro.groovy.customized

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.co.spel.NkSpELManager
import cn.nkpro.ts5.docengine.NkAbstractCard
import cn.nkpro.ts5.docengine.model.DocDefIV
import cn.nkpro.ts5.docengine.model.DocHV
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(10)
@NkNote("中信建投定制抬头")
@Component("CiticCardHeader")
class CiticCardHeader extends NkAbstractCard<Map,Def> {

    @Autowired
    private NkSpELManager spELManager

    @Override
    String getPosition() {
        return POSITION_HEADER
    }

    @Override
    Map afterGetData(DocHV doc, Map data, DocDefIV defIV, Def d) {

        if(StringUtils.isNotBlank(d.getPartnerNameSpEL())){
            if(StringUtils.isBlank(doc.getPartnerName())){
                def context = spELManager.createContext(doc)
                doc.setPartnerName(spELManager.invoke(d.getPartnerNameSpEL(), context) as String)
            }
        }

        if(StringUtils.isNotBlank(d.getDocNameSpEL())){
            def context = spELManager.createContext(doc)
            doc.setDocName(spELManager.invoke(d.getDocNameSpEL(), context) as String)
        }

        return super.afterGetData(doc, data, defIV, d) as Map
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

        if(StringUtils.isNotBlank(d.getDocNameSpEL())){
            doc.setDocName(spELManager.invoke(d.getDocNameSpEL(), context) as String)
        }

        return data
    }

    static class Def{
        String partnerIdSpEL
        String partnerNameSpEL
        String docNameSpEL
    }
}
