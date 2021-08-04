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

    DocHV calculate(DocHV doc, String fromCard, String options);

    DocHV detail  (DocDefHV def, DocHD docHD);

    DocHV toCreate(DocDefHV def, DocHV preDoc);

    DocHV doUpdate(DocHV doc, DocHV original, String optSource);

    void doOnBpmKilled(DocHV docHV, String processKey, String optSource);
}
