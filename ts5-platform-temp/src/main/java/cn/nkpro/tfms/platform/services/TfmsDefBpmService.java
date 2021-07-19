package cn.nkpro.tfms.platform.services;

import org.flowable.engine.repository.Deployment;

import java.io.IOException;
import java.util.List;

public interface TfmsDefBpmService {
    Deployment deploy(
            String definitionId,
            String key,
            String resourceName,
            String bpmnXml);

    List<Object> getAllDeployments();

    String getBpmnXml(String deploymentId, String resourceName) throws IOException;
}
