package cn.nkpro.ts5.engine.bpm;


import cn.nkpro.ts5.engine.bpm.model.BpmDeployment;
import cn.nkpro.ts5.engine.bpm.model.BpmProcessDefinition;

import java.util.List;

public interface NkBpmDefService {

    BpmDeployment deploy(BpmProcessDefinition definition);

    BpmProcessDefinition getProcessDefinition(String definitionId);

    List<Object> getAllDeployments();
}
