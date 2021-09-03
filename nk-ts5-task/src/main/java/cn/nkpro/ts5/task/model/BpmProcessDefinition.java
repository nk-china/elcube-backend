package cn.nkpro.ts5.task.model;

import lombok.Data;

@Data
public class BpmProcessDefinition {

        private String id;
        private String deploymentId;
        private String key;
        private String name;
        private Integer version;
        private String resourceName;
        private String bpmnXml;
        private String fromId;
}