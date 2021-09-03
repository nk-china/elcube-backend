package cn.nkpro.ts5.docengine.service;

import cn.nkpro.ts5.docengine.gen.SysLogDocRecord;
import cn.nkpro.ts5.docengine.model.DocHHistory;
import cn.nkpro.ts5.docengine.model.DocHV;

import java.util.List;

public interface NkDocHistoryService {

    void doAddVersion(DocHV doc, DocHV original, List<String> changedCard, String source);

    List<SysLogDocRecord> getHistories(String docId, int offset);

    DocHHistory getDetail(String historyId);
}
