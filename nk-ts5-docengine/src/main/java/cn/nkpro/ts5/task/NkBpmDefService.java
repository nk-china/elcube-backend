package cn.nkpro.ts5.task;


import cn.nkpro.ts5.task.model.ResourceDefinition;
import cn.nkpro.ts5.task.model.ResourceDeployment;

public interface NkBpmDefService {

    ResourceDeployment deploy(ResourceDefinition definition);

    ResourceDefinition getProcessDefinition(String definitionId);

    ResourceDefinition getDmnDefinition(String definitionId);
}
