package cn.nkpro.groovy.cards.simple

import cn.nkpro.ts5.docengine.abstracts.NkAbstractCard
import cn.nkpro.ts5.docengine.model.DocDefHV
import cn.nkpro.ts5.docengine.model.DocHV
import cn.nkpro.ts5.annotation.NkNote
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@NkNote("日期")
@Component("NkCardDate")
class NkCardDate extends NkAbstractCard<Map,Map> {

    Logger logger = LoggerFactory.getLogger(getClass());

    String date;

    NkCardDate(){
    }
    //@Override
    String getComponentDesc() {
        return null
    }

    //@Override
    protected Map doGetData(DocHV doc, Map o) throws Exception {
        return null
    }

    //@Override
    protected Map toCreate(DocHV doc, DocHV preDoc, DocDefHV docDef, Map o) throws Exception {
        return null
    }

    //@Override
    protected void doUpdate(DocHV doc, DocDefHV docDef, Map data, Map o) throws Exception {

    }


}
