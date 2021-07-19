package cn.nkpro.tfms.platform.model.bpm;

import lombok.Data;

import java.util.List;

/**
 * Created by bean on 2020/7/22.
 */
@Data
public class BpmTask {
    private String id;
    private String name;
    private String priority;
    private String taskDefinitionKey;
    private String processDefinitionId;
    private String processInstanceId;
    private String createTime;
    private String assignee;

    // instance props
    private String businessKey;
    
    private Boolean revokeAble = false;

    // doc props
    private String docId;
    private String docType;
    private String classify;

    // flows
    private List<BpmTaskFlow> flows;
}
