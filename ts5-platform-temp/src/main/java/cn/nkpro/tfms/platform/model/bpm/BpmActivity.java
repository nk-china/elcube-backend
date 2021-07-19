package cn.nkpro.tfms.platform.model.bpm;

import lombok.Data;

import java.util.List;

@Data
public class BpmActivity {
    private String taskId;
    private String activityName;
    private Long startDate;
    private Long endDate;
    private String assignee;
    private String user;

    private List<BpmComment> comments;
}
