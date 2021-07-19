package cn.nkpro.ts5.config.nk;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by bean on 2020/7/28.
 */
@Data
@ConfigurationProperties(prefix = "nk")
public class NKProperties {
    private String envKey;
    private String envName;
    private Boolean responseCompress = false;
    private String responseCompressSecret;


    private String fileRootPath;
    private String passwordStrategy;
    private Boolean enableBreachStream;
}

