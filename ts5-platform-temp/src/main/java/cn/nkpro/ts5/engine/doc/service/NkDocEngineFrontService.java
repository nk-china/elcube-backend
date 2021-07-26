package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.engine.doc.model.DocHV;

public interface NkDocEngineFrontService {
    DocHV toCreate(String docType, String preDocId) throws Exception;
}
