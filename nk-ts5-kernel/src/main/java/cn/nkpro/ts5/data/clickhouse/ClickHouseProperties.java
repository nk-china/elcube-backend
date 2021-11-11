package cn.nkpro.ts5.data.clickhouse;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "nk.clickhouse")
public class ClickHouseProperties {
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private HikariConfig hikari;
}
