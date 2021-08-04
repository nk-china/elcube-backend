package cn.nkpro.ts5.engine.task;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.engine.task.model.BpmInstance;
import cn.nkpro.ts5.engine.task.model.BpmTask;
import cn.nkpro.ts5.engine.task.model.BpmTaskComplete;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NkBpmTaskService {

    PageList<BpmInstance> processInstancePage(Integer from, Integer rows);

    BpmInstance processInstanceDetail(String instanceId);

    void deleteProcessInstance(String instanceId, String deleteReason);


    @Transactional
    ProcessInstance start(String key, String docId);

    @Transactional
    void complete(BpmTaskComplete bpmTask);

    BpmTask taskByBusinessAndAssignee(String businessKey, String assignee);

    Boolean taskExists(String taskId);

    List<BpmInstance> getProcessInfoByDocId(String docId);

    void reIndexTask();

    void revoke(String instanceId);
}
