package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.co.NkCustomObject;
import cn.nkpro.ts5.docengine.model.DocDefHV;
import cn.nkpro.ts5.docengine.model.DocHBasis;
import cn.nkpro.ts5.docengine.model.DocHPersistent;
import cn.nkpro.ts5.docengine.model.DocHV;

public interface NkDocProcessor extends NkCustomObject {

    enum EnumDocClassify {
        PARTNER,
        TRANSACTION
    }
    EnumDocClassify classify();

    DocHV detail(DocDefHV def, DocHBasis docHD);

    DocHV toCreate(DocDefHV def, DocHV preDoc);

    DocHV doUpdate(DocHV doc, DocHV original, String optSource);

    DocHV calculate(DocHV doc, String fromCard, Object options);

    Object call(DocHV doc, String fromCard, String method, Object options);

    void doOnBpmKilled(DocHV docHV, String processKey, String optSource);

    DocHV random(DocHV doc);

    DocHBasis deserialize(DocDefHV def, DocHPersistent docHD);
}
