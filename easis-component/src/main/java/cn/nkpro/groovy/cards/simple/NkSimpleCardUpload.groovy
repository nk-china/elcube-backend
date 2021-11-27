package cn.nkpro.groovy.cards.simple

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import cn.nkpro.easis.docengine.NkDocEngine
import cn.nkpro.easis.docengine.model.DocDefHV
import cn.nkpro.easis.docengine.model.DocDefIV
import cn.nkpro.easis.docengine.model.DocHV
import com.alibaba.fastjson.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("文件上传演示")
@Component("NkSimpleCardUpload")
class NkSimpleCardUpload extends NkAbstractCard<Map,Map> {

    @Autowired
    NkDocEngine docEngine;

    @Override
    Map afterGetDef(DocDefHV defHV, DocDefIV defIV, Map cardDef) {
        log.debug("DEF:"+JSON.toJSONString(cardDef))
        return cardDef
    }

    @Override
    Map afterCreate(DocHV doc, DocHV preDoc, Map data, DocDefIV defIV, Map cardDef) {
        log.debug("DATA:"+JSON.toJSONString(data))
        return data
    }

    @Override
    Map afterGetData(DocHV doc, Map data, DocDefIV defIV, Map cardDef) {
        log.debug("DATA:"+JSON.toJSONString(data))
        return data
    }

    @Override
    Map beforeUpdate(DocHV doc, Map data, Map original, DocDefIV defIV, Map cardDef) {
        log.debug("DATA:"+JSON.toJSONString(data))
        return data
    }
}
