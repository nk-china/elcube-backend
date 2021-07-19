package cn.nkpro.ts5.config.mybatis;

import cn.nkpro.ts5.config.mybatis.pagination.dialect.Dialect;
import cn.nkpro.ts5.config.mybatis.pagination.PaginationInterceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
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
@EnableConfigurationProperties(NKMybatisProperties.class)
@Qualifier("MyBatisConfiguration")
public class NKMyBatisConfiguration {

    @Autowired
    private NKMybatisProperties properties;

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        PaginationInterceptor interceptor = new PaginationInterceptor();
        try {
            interceptor.setDialect((Dialect) Class.forName(properties.getDialect()).getConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
        return interceptor;
    }
}
