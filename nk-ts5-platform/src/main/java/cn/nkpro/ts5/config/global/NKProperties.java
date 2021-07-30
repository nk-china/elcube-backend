package cn.nkpro.ts5.config.global;

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

    private String fileRootPath;
    private String passwordStrategy;
    private Boolean enableBreachStream;

    private boolean  componentDisableOnlineEditing = true;
    private boolean  componentReloadClassPath      = true;
    private String[] componentBasePackages         = new String[]{"cn.nkpro.ts5.cards"};
}

