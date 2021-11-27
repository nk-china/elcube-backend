package cn.nkpro.easis.task;


import cn.nkpro.easis.task.model.ResourceDefinition;
import cn.nkpro.easis.task.model.ResourceDeployment;

public interface NkBpmDefService {

    ResourceDeployment deploy(ResourceDefinition definition);

    ResourceDefinition getProcessDefinition(String definitionId);

    ResourceDefinition getDmnDefinition(String definitionId);
}
