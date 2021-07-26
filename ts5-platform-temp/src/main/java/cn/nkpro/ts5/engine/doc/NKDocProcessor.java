package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.basic.NKCustomObject;
import cn.nkpro.ts5.engine.doc.model.DocDefHV;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import lombok.Getter;

public interface NKDocProcessor extends NKCustomObject {

    enum EnumDocClassify {
        PARTNER,
        TRANSACTION
    }
    EnumDocClassify classify();

    DocHV toCreate(DocDefHV def, DocHV preDoc) throws Exception;

    DocHV detail(DocDefHV def, String docId);
}
