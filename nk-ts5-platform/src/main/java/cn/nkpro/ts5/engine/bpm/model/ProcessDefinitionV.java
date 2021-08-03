package cn.nkpro.ts5.engine.bpm.model;

import lombok.Data;

@Data
public class ProcessDefinitionV{
        private String id;
        private String deploymentId;
        private String key;
        private String name;
        private Integer version;
        private String resourceName;
        private String bpmnXml;
        private String fromId;
}