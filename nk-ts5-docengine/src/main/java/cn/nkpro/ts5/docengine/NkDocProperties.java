package cn.nkpro.ts5.docengine;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Created by bean on 2020/7/28.
 */
@Data
@ConfigurationProperties(prefix = "nk.doc")
public class NkDocProperties {

    private Map<String,Class> indices;
}

