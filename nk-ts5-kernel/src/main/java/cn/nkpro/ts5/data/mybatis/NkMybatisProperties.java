package cn.nkpro.ts5.data.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mybatis")
public class NkMybatisProperties {
    private String dialect = "cn.nkpro.ts5.config.mybatis.pagination.dialect.MySQLDialect";
}
