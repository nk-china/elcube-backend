package cn.nkpro.ts5.cards

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.engine.doc.NKAbstractCard
import cn.nkpro.ts5.engine.doc.model.DocDefHV
import cn.nkpro.ts5.engine.doc.model.DocHV
import cn.nkpro.ts5.model.mb.gen.DocDefI
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@WsDocNote("日期")
@Component("NkCardDate")
class NkCardDate extends NKAbstractCard<Map,Map> {

    Logger logger = LoggerFactory.getLogger(getClass());

    String date;

    NkCardDate(){

        def a = 1
        def b = new StringBuilder()

        b = 1

        println b

        logger.debug(a as String);

    }

    @Override
    String[] getDefComponentNames() {
        return ["NkCardDateDef"];
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
