package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.engine.doc.NkDocEngine;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface NkDocEngineFrontService extends NkDocEngine {

    DocHV detailView(String docId);

    @Transactional(propagation = Propagation.NEVER)
    Object call(DocHV doc, String fromCard, String method, String options);

    @Transactional
    DocHV doUpdateView(DocHV docHV, String optSource);

    void onBpmKilled(String docId, String processKey, String optSource);
}
