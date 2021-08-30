package cn.nkpro.ts5.engine.task;

import cn.nkpro.ts5.engine.task.model.BpmTask;
import cn.nkpro.ts5.engine.task.model.BpmTaskComplete;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

public interface NkBpmTaskService {
    @Transactional
    ProcessInstance start(String key, String docId);

    @Transactional
    void complete(BpmTaskComplete bpmTask);

    BpmTask taskByBusinessAndAssignee(String businessKey, String assignee);
}
