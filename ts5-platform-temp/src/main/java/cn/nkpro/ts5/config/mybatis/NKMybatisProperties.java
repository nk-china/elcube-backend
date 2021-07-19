package cn.nkpro.ts5.config.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "mybatis")
public class NKMybatisProperties {
    private String dialect = "cn.nkpro.ts5.config.mybatis.pagination.dialect.MySQLDialect";
}
