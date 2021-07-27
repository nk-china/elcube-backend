package cn.nkpro.ts5.cards.simple;

import cn.nkpro.ts5.basic.wsdoc.annotation.WsDocNote;
import cn.nkpro.ts5.engine.doc.NKAbstractCard;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocDefIV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@WsDocNote("测试")
@Component("NkCardSimple")
public class NkCardSimple extends NKAbstractCard<NkCardSimpleData,NkCardSimpleDef> {

    @Override
    public String[] getDefComponentNames() {
        return new String[]{"NkCardSimpleDef"};
    }

    @Override
    public NkCardSimpleDef afterGetDef(DocDefHV defHV, DocDefIV defIV, NkCardSimpleDef def) {
        log.info("DEF:"+JSON.toJSONString(def));
        return def;
    }

    @Override
    public NkCardSimpleData afterCreate(DocHV doc, DocHV preDoc, NkCardSimpleData data, NkCardSimpleDef def) {
        log.info("DATA:"+JSON.toJSONString(data));
        return super.afterCreate(doc, preDoc, data, def);
    }

    @Override
    public NkCardSimpleData afterGetData(DocHV doc, NkCardSimpleData data, NkCardSimpleDef def) {
        log.info("DATA:"+JSON.toJSONString(data));
        return super.afterGetData(doc, data, def);
    }

    @Override
    public NkCardSimpleData beforeUpdate(DocHV doc, NkCardSimpleData data, NkCardSimpleDef def, NkCardSimpleData original) {
        log.info("DATA:"+JSON.toJSONString(data));
        return super.beforeUpdate(doc, data, def, original);
    }

    @Override
    public void afterUpdated(DocHV doc, NkCardSimpleData data, NkCardSimpleDef def) {
        log.info("DATA:"+JSON.toJSONString(data));
    }
}
