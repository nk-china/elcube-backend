package cn.nkpro.easis.task.impl;

import cn.nkpro.easis.exception.NkSystemException;
import cn.nkpro.easis.task.NkBpmTaskService;
import cn.nkpro.easis.task.model.BpmTask;
import cn.nkpro.easis.task.model.BpmTaskComplete;


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
