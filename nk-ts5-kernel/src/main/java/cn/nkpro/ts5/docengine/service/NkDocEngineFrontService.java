package cn.nkpro.ts5.docengine.service;

import cn.nkpro.ts5.docengine.NkDocEngine;
import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.gen.DocH;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface NkDocEngineFrontService extends NkDocEngine {

    PageList<DocH> list(String docType, int offset, int rows, String orderBy);

    DocHV detailView(String docId);

    @Transactional(propagation = Propagation.NEVER)
    Object call(DocHV doc, String fromCard, String method, String options);

    @Transactional
    DocHV doUpdateView(DocHV docHV, String optSource);

    void onBpmKilled(String docId, String processKey, String optSource);

    void reDataSync(DocHV doc);
}
