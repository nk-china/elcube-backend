package cn.nkpro.ts5.config.nk;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "nk.huawei")
public class HuaweiProperties {

    private String accessKeyId;
    private String accessKeySecret;

    @NestedConfigurationProperty
    private OBS obs;

    @Data
    public static class OBS{
        @Getter
        private String bucket = "tfms";
        private String endpoint = "obs.cn-north-4.myhuaweicloud.com";
        private String host = "https://tfms.obs.cn-north-4.myhuaweicloud.com";
        /**
         * 上传到OSS的根目录路径，不能以/开头，必须以/结尾
         */
        private String path = "";
    }
}
