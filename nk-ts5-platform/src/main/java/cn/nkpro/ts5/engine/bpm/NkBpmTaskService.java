package cn.nkpro.ts5.engine.bpm;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.engine.bpm.model.BpmInstance;
import cn.nkpro.ts5.engine.bpm.model.BpmTask;
import cn.nkpro.ts5.engine.bpm.model.BpmTaskComplete;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NkBpmTaskService {

    @Transactional
    ProcessInstance start(String key, String docId);

    @Transactional
    void complete(BpmTaskComplete bpmTask);

    PageList<BpmInstance> processInstancePage(Integer from, Integer rows);

    BpmInstance processInstanceDetail(String instanceId);

    Boolean taskExists(String taskId);

    BpmTask task(String taskId);

    List<BpmInstance> getProcessInfoByDocId(String docId);

    void reIndexTask();

    void revoke(String instanceId);

    void deleteProcessInstance(String instanceId, String deleteReason);
}
