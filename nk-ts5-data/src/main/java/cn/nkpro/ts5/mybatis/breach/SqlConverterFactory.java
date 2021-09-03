package cn.nkpro.ts5.mybatis.breach;

import cn.nkpro.ts5.mybatis.breach.converter.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SqlConverterFactory {
	private static final SqlConverterFactory global = new SqlConverterFactory();
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public static SqlConverterFactory getGlobalInstance() {
        return global;
    }
	
	private Map<Type, SqlConverter> converterMap = new HashMap<Type, SqlConverter>();
	
	private SqlConverterFactory() {
		converterMap.put(Insert.class, new InsertSqlConverter());
		converterMap.put(Update.class, new UpdateSqlConverter());
		converterMap.put(Delete.class, new DeleteSqlConverter());
		converterMap.put(Select.class, new SelectSqlConverter());
	}
	
	public String convert(BoundSql boundSql, MappedStatement mappedStatement,Map<String, Strategy> strategies) {
		String sql = boundSql.getSql();
		Object params = boundSql.getParameterObject();
		String mapperId = mappedStatement.getId();
		logger.debug("[Modify the sql before] " + sql);
		try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			if(statement != null) {
				return getConverter(statement.getClass()).convert(statement, params, mapperId, strategies);
			}
		} catch (JSQLParserException e) {
			logger.error("[convert -> JSQLParserException]", e);
		}
		return sql;
	}
	
	public SqlConverter getConverter(Type type) {
		SqlConverter converter = converterMap.get(type);
		if(converter == null){
			converter = new NullSqlConverter();
		}
		return converter;
	}
}
