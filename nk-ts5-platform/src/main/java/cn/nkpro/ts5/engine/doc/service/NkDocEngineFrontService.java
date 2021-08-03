package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.engine.doc.model.DocHV;

public interface NkDocEngineFrontService {

    DocHV detail(String docId);

    DocHV create(String docType, String preDocId) throws Exception;

    DocHV calculate(DocHV doc, String fromCard, String options) throws Exception;

    DocHV doUpdate(DocHV doc) throws Exception;

    void onBpmKilled(String docId, String processKey, String optSource);
}
