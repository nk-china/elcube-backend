package cn.nkpro.easis.task.model;

import lombok.Data;

import java.util.Date;

@Data
public class ResourceDeployment {
    private String id;
    private String name;
    private Date deploymentTime;
    private boolean isNew;
    private String source;
    private String tenantId;
}
