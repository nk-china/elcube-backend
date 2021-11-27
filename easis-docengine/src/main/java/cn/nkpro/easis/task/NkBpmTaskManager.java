package cn.nkpro.easis.task;

import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.docengine.model.DocHV;
import cn.nkpro.easis.task.model.BpmInstance;

public interface NkBpmTaskManager {

    PageList<BpmInstance> processInstancePage(Integer from, Integer rows);

    BpmInstance processInstanceDetail(String instanceId);

    void deleteProcessInstance(String instanceId, String deleteReason);



    Boolean taskExists(String taskId);

    void indexDocTask(DocHV doc);

    void revoke(String instanceId);
}
