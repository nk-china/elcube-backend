/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.data.clickhouse;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    @ConditionalOnProperty("nk.clickhouse.url")
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
