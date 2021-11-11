package cn.nkpro.ts5.data.clickhouse;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ClickHouseTemplate extends JdbcTemplate {
    ClickHouseTemplate(DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }
}