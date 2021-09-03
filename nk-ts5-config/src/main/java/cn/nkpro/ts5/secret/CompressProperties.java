package cn.nkpro.ts5.secret;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by bean on 2020/7/28.
 */
@Data
@ConfigurationProperties(prefix = "nk.compress")
public class CompressProperties {
    private Boolean enabled = false;
}

