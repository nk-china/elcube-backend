package cn.nkpro.ts5.cards

import cn.nkpro.tfms.platform.custom.TfmsAbstractCard
import cn.nkpro.tfms.platform.model.BizDocBase
import cn.nkpro.tfms.platform.model.DefDocTypeBO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NkCardDate extends TfmsAbstractCard<Map,Map>{

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
    String getComponentDesc() {
        return null
    }

    @Override
    protected Map doGetData(BizDocBase doc, Map o) throws Exception {
        return null
    }

    @Override
    protected Map toCreate(BizDocBase doc, BizDocBase preDoc, DefDocTypeBO docDef, Map o) throws Exception {
        return null
    }

    @Override
    protected void doUpdate(BizDocBase doc, DefDocTypeBO docDef, Map data, Map o) throws Exception {

    }
}
