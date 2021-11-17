package cn.nkpro.ts5.task;

import cn.nkpro.ts5.task.model.BpmTask;
import cn.nkpro.ts5.task.model.BpmTaskComplete;
import org.springframework.transaction.annotation.Transactional;

public interface NkBpmTaskService {
    @Transactional
    String start(String key, String docId);

    @Transactional
    void complete(BpmTaskComplete bpmTask);

    BpmTask taskByBusinessAndAssignee(String businessKey, String assignee);
}
