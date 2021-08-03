package cn.nkpro.groovy.cards.simple

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.engine.doc.abstracts.NkAbstractCard
import cn.nkpro.ts5.engine.doc.model.DocDefHV
import cn.nkpro.ts5.engine.doc.model.DocDefIV
import cn.nkpro.ts5.engine.doc.model.DocHV
import cn.nkpro.ts5.engine.doc.service.NkDocEngineFrontService
import com.alibaba.fastjson.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@WsDocNote("测试")
@Component("NkCardSimple")
class NkCardSimple extends NkAbstractCard<NkCardSimpleData,NkCardSimpleDef> {

    @Autowired
    NkDocEngineFrontService docEngineFrontService;

    @Override
    NkCardSimpleDef afterGetDef(DocDefHV defHV, DocDefIV defIV, NkCardSimpleDef cardDef) {
        log.info("DEF:"+JSON.toJSONString(cardDef))
        return cardDef
    }

    @Override
    NkCardSimpleData afterCreate(DocHV doc, DocHV preDoc, NkCardSimpleData data, NkCardSimpleDef cardDef) {
        log.info("DATA:"+JSON.toJSONString(data))
        return data
    }

    @Override
    NkCardSimpleData afterGetData(DocHV doc, NkCardSimpleData data, NkCardSimpleDef cardDef) {
        log.info("DATA:"+JSON.toJSONString(data))
        return data
    }

    @Override
    NkCardSimpleData beforeUpdate(DocHV doc, NkCardSimpleData data, NkCardSimpleDef cardDef, NkCardSimpleData original) {
        log.info("DATA:"+JSON.toJSONString(data))
        return data
    }

    static class NkCardSimpleData {

        private String field

        String getField() {
            return field
        }

        void setField(String field) {
            this.field = field
        }
    }

    static class NkCardSimpleDef {
        private String field

        String getField() {
            return field
        }

        void setField(String field) {
            this.field = field
        }
    }
}
