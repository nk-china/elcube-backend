package cn.nkpro.ts5.engine.task;

import cn.nkpro.ts5.basic.PageList;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.task.model.BpmInstance;
import cn.nkpro.ts5.engine.task.model.BpmTask;
import cn.nkpro.ts5.engine.task.model.BpmTaskComplete;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NkBpmTaskManager {

    PageList<BpmInstance> processInstancePage(Integer from, Integer rows);

    BpmInstance processInstanceDetail(String instanceId);

    void deleteProcessInstance(String instanceId, String deleteReason);



    Boolean taskExists(String taskId);

    void indexDocTask(DocHV doc);

    void revoke(String instanceId);
}
