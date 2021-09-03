package cn.nkpro.ts5.task;


import cn.nkpro.ts5.task.model.BpmDeployment;
import cn.nkpro.ts5.task.model.BpmProcessDefinition;

public interface NkBpmDefService {

    BpmDeployment deploy(BpmProcessDefinition definition);

    BpmProcessDefinition getProcessDefinition(String definitionId);
}
