package cn.nkpro.tfms.platform.model.bpm;

import lombok.Data;

@Data
public class BpmComment {
    private String id;
    private Long time;
    private String comment;
    private String userId;
    private String user;
}
