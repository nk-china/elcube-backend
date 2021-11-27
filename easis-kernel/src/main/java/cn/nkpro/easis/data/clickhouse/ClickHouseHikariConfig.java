package cn.nkpro.easis.data.clickhouse;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "nk.clickhouse.hikari")
public class ClickHouseHikariConfig extends HikariConfig {
}
