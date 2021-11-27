package cn.nkpro.easis.docengine.service;

import cn.nkpro.easis.docengine.gen.DocRecord;
import cn.nkpro.easis.docengine.model.DocHHistory;
import cn.nkpro.easis.docengine.model.DocHV;

import java.util.List;

public interface NkDocHistoryService {

    void doAddVersion(DocHV doc, DocHV original, List<String> changedCard, String source);

    List<DocRecord> getHistories(String docId, int offset);

    DocHHistory getDetail(String historyId);
}
