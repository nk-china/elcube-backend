package cn.nkpro.tfms.platform.model;

import lombok.Data;

@Data
public class NkFileUploadStatus {
    private String name;
    private String status;
    private String thumbUrl;
    private String url;
    private String message;
}
