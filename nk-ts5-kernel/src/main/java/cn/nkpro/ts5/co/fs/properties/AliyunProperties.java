package cn.nkpro.ts5.co.fs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "nk.aliyun")
public class AliyunProperties {

    private String accessKeyId;
    private String accessKeySecret;

    @NestedConfigurationProperty
    private OSS oss;

    @Data
    public static class OSS{
        private String bucket = "tfms";
        private String endpoint = "oss-cn-beijing.aliyuncs.com";
        private String host = "https://tfms.oss-cn-beijing.aliyuncs.com";
        /**
         * 上传到OSS的根目录路径，不能以/开头，必须以/结尾
         */
        private String path = "";
    }
}
