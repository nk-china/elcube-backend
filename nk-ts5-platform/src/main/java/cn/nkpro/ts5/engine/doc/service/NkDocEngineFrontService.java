package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.engine.doc.NkDocEngine;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import org.springframework.transaction.annotation.Transactional;

public interface NkDocEngineFrontService extends NkDocEngine {

    DocHV detailView(String docId);

    @Transactional
    DocHV doUpdateView(DocHV docHV);

    void onBpmKilled(String docId, String processKey, String optSource);
}
