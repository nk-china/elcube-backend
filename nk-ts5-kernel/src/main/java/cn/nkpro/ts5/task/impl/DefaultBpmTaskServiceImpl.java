package cn.nkpro.ts5.task.impl;

import cn.nkpro.ts5.task.model.BpmTask;
import cn.nkpro.ts5.task.model.BpmTaskComplete;
import cn.nkpro.ts5.task.NkBpmTaskService;
import cn.nkpro.ts5.exception.NkSystemException;


public class DefaultBpmTaskServiceImpl implements NkBpmTaskService {
    @Override
    public String start(String key, String docId) {
        throw new NkSystemException("操作不支持");
    }

    @Override
    public void complete(BpmTaskComplete bpmTask) {
        throw new NkSystemException("操作不支持");
    }

    @Override
    public BpmTask taskByBusinessAndAssignee(String businessKey, String assignee) {
        throw new NkSystemException("操作不支持");
    }
}
