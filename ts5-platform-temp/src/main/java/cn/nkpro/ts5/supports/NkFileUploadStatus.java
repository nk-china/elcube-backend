package cn.nkpro.ts5.supports;

import lombok.Data;

@Data
public class NkFileUploadStatus {
    private String name;
    private String status;
    private String thumbUrl;
    private String url;
    private String message;
}
