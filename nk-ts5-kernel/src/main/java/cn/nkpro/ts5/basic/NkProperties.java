package cn.nkpro.ts5.basic;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by bean on 2020/7/28.
 */
@Data
@ConfigurationProperties(prefix = "nk")
public class NkProperties {

    private String secretId;
    private String secretKey;

    private String envKey;
    private String envName;

    private String fileRootPath;
    private String passwordStrategy;
    private Boolean enableBreachStream;

    private boolean  componentDisableOnlineEditing = false;
    private boolean  componentReloadClassPath      = false;
    private String[] componentBasePackages         = new String[]{"cn.nkpro.ts5.cards"};
}

