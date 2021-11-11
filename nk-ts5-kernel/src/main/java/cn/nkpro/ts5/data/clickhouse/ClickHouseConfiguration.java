package cn.nkpro.ts5.data.clickhouse;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties({
        ClickHouseProperties.class,
        ClickHouseHikariConfig.class
})
public class ClickHouseConfiguration {

    @ConfigurationProperties(prefix = "nk.clickhouse.url")
    @Bean("clickHouseDataSource")
    public DataSource clickHouseDataSource(ClickHouseProperties properties){

        HikariConfig hikari = properties.getHikari();

        if(hikari==null){
            hikari = new HikariConfig();
        }

        if(StringUtils.isNotBlank(properties.getUrl()))
            hikari.setJdbcUrl(properties.getUrl());
        if(StringUtils.isNotBlank(properties.getDriverClassName()))
            hikari.setDriverClassName(properties.getDriverClassName());
        if(StringUtils.isNotBlank(properties.getUsername()))
            hikari.setUsername(properties.getUsername());
        if(StringUtils.isNotBlank(properties.getPassword()))
            hikari.setPassword(properties.getPassword());

        return new HikariDataSource(hikari);
    }

    @ConditionalOnBean(name = "clickHouseDataSource")
    @Bean
    public ClickHouseTemplate clickHouseJdbcTemplate(@Qualifier("clickHouseDataSource") DataSource dataSource){
        return new ClickHouseTemplate(dataSource, true);
    }
}
