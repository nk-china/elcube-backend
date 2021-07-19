package cn.nkpro.ts5.config.mybatis.breach;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;

import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Intercepts(
//        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,Integer.class})
        @Signature(type = StatementHandler.class, method = "parameterize", args = {Statement.class})
)
@Slf4j
public class BreachTableInterceptor implements Interceptor {

    private static final String DELEGATE = "delegate";
    private static final String MAPPED_STATEMENT = "mappedStatement";

    // 分表策略缓存
    private ConcurrentHashMap<String, Strategy> strategies;

    public BreachTableInterceptor(Map<String, Strategy> strategies) {
        this.strategies = new ConcurrentHashMap<>(strategies);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        if(invocation.getTarget().getClass().isAssignableFrom(RoutingStatementHandler.class)) {
            final RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
            final BaseStatementHandler delegate = (BaseStatementHandler) Reflections.getFieldValue(statementHandler, DELEGATE);
            final MappedStatement mappedStatement = (MappedStatement) Reflections.getFieldValue(delegate, MAPPED_STATEMENT);
            final BoundSql boundSql = statementHandler.getBoundSql();
            String sql = SqlConverterFactory.getGlobalInstance().convert(boundSql, mappedStatement, strategies);
            if(StringUtils.isNotBlank(sql)) {
                log.debug("[Modified sql] " + sql);
                Reflections.setFieldValue(statementHandler.getBoundSql(), "sql", sql);
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
