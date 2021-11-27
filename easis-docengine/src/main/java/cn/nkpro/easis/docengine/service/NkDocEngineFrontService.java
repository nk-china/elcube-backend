package cn.nkpro.easis.docengine.service;

import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.docengine.NkDocEngine;
import cn.nkpro.easis.docengine.gen.DocH;
import cn.nkpro.easis.docengine.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface NkDocEngineFrontService extends NkDocEngine {

    PageList<DocH> list(String docType, int offset, int rows, String orderBy);

    DocHV detailView(String docId);

    @Transactional(propagation = Propagation.NEVER)
    Object call(DocHV doc, String fromCard, String method, Object options);

    @Transactional
    DocHV doUpdateView(DocHV docHV, String optSource);

    void onBpmKilled(String docId, String processKey, String optSource);

    void reDataSync(DocHV doc);
}
