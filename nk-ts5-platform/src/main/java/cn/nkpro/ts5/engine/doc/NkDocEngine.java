package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.doc.model.DocHV;

public interface NkDocEngine {

    DocHV detail(String docId);

    DocHV create(String docType, String preDocId);

    DocHV calculate(DocHV doc, String fromCard, String options);

    DocHV doUpdate(DocHV doc, String optSource);
}
