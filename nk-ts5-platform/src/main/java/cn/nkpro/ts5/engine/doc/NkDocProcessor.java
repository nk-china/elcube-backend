package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.co.NkCustomObject;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHD;
import cn.nkpro.ts5.engine.doc.model.DocHV;

public interface NkDocProcessor extends NkCustomObject {

    enum EnumDocClassify {
        PARTNER,
        TRANSACTION
    }
    EnumDocClassify classify();

    DocHV calculate(DocHV doc, String fromCard, String options) throws Exception;

    DocHV detail  (DocDefHV def, DocHD docHV) throws Exception;

    DocHV toCreate(DocDefHV def, DocHV preDoc) throws Exception;

    DocHV doUpdate(DocDefHV def, DocHV doc, DocHV original, String optSource) throws Exception;
}
