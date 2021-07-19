package cn.nkpro.tfms.platform.model.bpm;

import lombok.Data;

import java.util.List;

@Data
public class BpmInstance {

    private String id;
    private String name;
    private Long startDate;
    private Long endDate;
    private String startUserId;
    private String startUser;
    private String deleteReason;

    private Boolean revokeAble;

    private List<BpmActivity> activities;
}
