package cn.nkpro.easis.task;

import cn.nkpro.easis.task.model.BpmTask;
import cn.nkpro.easis.task.model.BpmTaskComplete;
import org.springframework.transaction.annotation.Transactional;

public interface NkBpmTaskService {
    @Transactional
    String start(String key, String docId);

    @Transactional
    void complete(BpmTaskComplete bpmTask);

    BpmTask taskByBusinessAndAssignee(String businessKey, String assignee);
}
