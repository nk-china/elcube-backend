package cn.nkpro.ts5.config.fs;

import lombok.Data;

@Data
public class FileUploadStatus {
    private String name;
    private String status;
    private String thumbUrl;
    private String url;
    private String message;
}
