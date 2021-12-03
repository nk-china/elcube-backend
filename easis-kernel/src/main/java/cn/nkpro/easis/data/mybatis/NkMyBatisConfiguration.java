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
package cn.nkpro.easis.data.mybatis;

import cn.nkpro.easis.data.mybatis.pagination.PaginationInterceptor;
import cn.nkpro.easis.data.mybatis.pagination.dialect.Dialect;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * 配置MyBatis分页拦截器
 * Created by bean on 2019/3/5.
 */
@Configuration
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@EnableConfigurationProperties(NkMybatisProperties.class)
@Qualifier("MyBatisConfiguration")
public class NkMyBatisConfiguration {

    @Bean
    public PaginationInterceptor paginationInterceptor(NkMybatisProperties properties){
        PaginationInterceptor interceptor = new PaginationInterceptor();
        try {
            interceptor.setDialect((Dialect) Class.forName(properties.getDialect()).getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
        return interceptor;
    }
}
