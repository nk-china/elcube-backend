package cn.nkpro.ts5.engine.bpm.model;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class BpmInstance {

    private String businessKey;
    private String state;
    private String id;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private Integer processDefinitionVersion;

    private Date startTime;
    private Date endTime;

    private Long startDate;
    private Long endDate;

    private String startUserId;
    private String startUser;
    private String deleteReason;

    private Boolean revokeAble;

    private List<BpmTask> bpmTask;

    private Map<String, Object> bpmVariables;

    private List<BpmActivity> activities;
}
