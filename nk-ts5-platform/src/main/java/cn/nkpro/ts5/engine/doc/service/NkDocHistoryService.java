package cn.nkpro.ts5.engine.doc.service;

import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.doc.model.DocHHistory;
import cn.nkpro.ts5.orm.mb.gen.SysLogDocRecord;

import java.util.List;

public interface NkDocHistoryService {

    void doAddVersion(DocHV doc, DocHV original, List<String> changedCard, String source);

    List<SysLogDocRecord> getHistories(String docId, int offset);

    DocHHistory getDetail(String historyId);
}
