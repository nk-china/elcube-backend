package cn.nkpro.ts5.docengine.service;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.docengine.model.BpmInstance;
import cn.nkpro.ts5.docengine.model.DocHV;

public interface NkBpmTaskManager {

    PageList<BpmInstance> processInstancePage(Integer from, Integer rows);

    BpmInstance processInstanceDetail(String instanceId);

    void deleteProcessInstance(String instanceId, String deleteReason);



    Boolean taskExists(String taskId);

    void indexDocTask(DocHV doc);

    void revoke(String instanceId);
}
