package cn.nkpro.groovy.cards.simple

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractCard
import cn.nkpro.ts5.docengine.NkDocEngine
import cn.nkpro.ts5.docengine.model.DocDefHV
import cn.nkpro.ts5.docengine.model.DocDefIV
import cn.nkpro.ts5.docengine.model.DocHV
import com.alibaba.fastjson.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("测试")
@Component("NkCardSimple")
class NkCardSimple extends NkAbstractCard<NkCardSimpleData,NkCardSimpleDef> {

    @Autowired
    NkDocEngine docEngine;

    @Override
    NkCardSimpleDef afterGetDef(DocDefHV defHV, DocDefIV defIV, NkCardSimpleDef cardDef) {
        log.debug("DEF:"+JSON.toJSONString(cardDef))
        return cardDef
    }

    @Override
    NkCardSimpleData afterCreate(DocHV doc, DocHV preDoc, NkCardSimpleData data, DocDefIV defIV, NkCardSimpleDef cardDef) {
        log.debug("DATA:"+JSON.toJSONString(data))
        return data
    }

    @Override
    NkCardSimpleData afterGetData(DocHV doc, NkCardSimpleData data, DocDefIV defIV, NkCardSimpleDef cardDef) {
        log.debug("DATA:"+JSON.toJSONString(data))
        return data
    }

    @Override
    NkCardSimpleData beforeUpdate(DocHV doc, NkCardSimpleData data, NkCardSimpleData original, DocDefIV defIV, NkCardSimpleDef cardDef) {
        log.debug("DATA:"+JSON.toJSONString(data))
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
