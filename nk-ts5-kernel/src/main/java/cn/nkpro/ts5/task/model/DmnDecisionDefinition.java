package cn.nkpro.ts5.task.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DmnDecisionDefinition extends ResourceDefinition {

        private String decisionRequirementsDefinitionId;
        private String decisionRequirementsDefinitionKey;
        private String versionTag;
}