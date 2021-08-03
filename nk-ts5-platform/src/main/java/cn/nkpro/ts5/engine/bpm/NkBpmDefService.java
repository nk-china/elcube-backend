package cn.nkpro.ts5.engine.bpm;


import cn.nkpro.ts5.engine.bpm.model.DeploymentV;
import cn.nkpro.ts5.engine.bpm.model.ProcessDefinitionV;
import org.camunda.bpm.engine.repository.Deployment;

import java.util.List;

public interface NkBpmDefService {

    DeploymentV deploy(ProcessDefinitionV definition);

    ProcessDefinitionV getProcessDefinition(String definitionId);

    List<Object> getAllDeployments();
}
