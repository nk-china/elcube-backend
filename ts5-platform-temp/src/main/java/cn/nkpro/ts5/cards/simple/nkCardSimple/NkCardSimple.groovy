package cn.nkpro.ts5.cards.simple.nkCardSimple

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote
import cn.nkpro.ts5.engine.doc.NKAbstractCard
import cn.nkpro.ts5.engine.doc.model.DocDefHV
import cn.nkpro.ts5.engine.doc.model.DocDefIV
import cn.nkpro.ts5.engine.doc.model.DocHV
import com.alibaba.fastjson.JSON
import org.springframework.stereotype.Component

@WsDocNote("测试")
@Component("NkCardSimple")
class NkCardSimple extends NKAbstractCard<NkCardSimpleData,NkCardSimpleDef> {

    @Override
    String[] getDefComponentNames() {
        return ["NkCardSimpleDef"]
    }

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

    @Override
    void afterUpdated(DocHV doc, NkCardSimpleData data, NkCardSimpleDef cardDef) {
        log.info("DATA:"+JSON.toJSONString(data))
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
