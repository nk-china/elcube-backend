package cn.nkpro.ts5.engine.task;


import cn.nkpro.ts5.engine.task.model.BpmDeployment;
import cn.nkpro.ts5.engine.task.model.BpmProcessDefinition;

import java.util.List;

public interface NkBpmDefService {

    BpmDeployment deploy(BpmProcessDefinition definition);

    BpmProcessDefinition getProcessDefinition(String definitionId);
}
