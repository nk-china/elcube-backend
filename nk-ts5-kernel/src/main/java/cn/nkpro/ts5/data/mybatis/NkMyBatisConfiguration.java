package cn.nkpro.ts5.data.mybatis;

import cn.nkpro.ts5.data.mybatis.pagination.PaginationInterceptor;
import cn.nkpro.ts5.data.mybatis.pagination.dialect.Dialect;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;

/**
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
