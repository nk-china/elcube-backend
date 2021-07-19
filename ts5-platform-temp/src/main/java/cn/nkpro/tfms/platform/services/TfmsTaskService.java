package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.model.bpm.BpmInstance;
import cn.nkpro.tfms.platform.model.bpm.BpmTask;
import cn.nkpro.tfms.platform.model.bpm.BpmTaskComplete;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TfmsTaskService {

    @Transactional
    void start(String key, String docId);

    @Transactional
    void complete(BpmTaskComplete bpmTask);

    Boolean taskExists(String taskId);

    BpmTask task(String taskId);

    List<BpmInstance> getProcessInfoByDocId(String docId);

    void reIndexTask();

    void revoke(String instanceId);

}
