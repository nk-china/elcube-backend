package cn.nkpro.easis.components.defaults.cards

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import cn.nkpro.easis.docengine.model.DocDefHV
import cn.nkpro.easis.docengine.model.DocHV
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
