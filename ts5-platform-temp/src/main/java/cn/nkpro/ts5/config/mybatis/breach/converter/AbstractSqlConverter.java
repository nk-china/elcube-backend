package cn.nkpro.ts5.config.mybatis.breach.converter;

import cn.nkpro.ts5.config.mybatis.breach.Strategy;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * SQL转换器抽象类
 * @author janjan, xujian_jason@163.com
 *
 */
public abstract class AbstractSqlConverter implements SqlConverter {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 做真正的转换
	 * @param statement
	 * @param params
	 * @return
	 */
	public abstract Statement doConvert(final Statement statement, final Object params,final Map<String, Strategy> strategies);

	/**
	 * Statement对象反解析成sql语句
	 * @param statement
	 * @return
	 */
	private String doDeParse(Statement statement) {
		if(statement == null) return null;
		StatementDeParser deParser = new StatementDeParser(new StringBuilder());
		statement.accept(deParser);
		return deParser.getBuffer().toString();
	}
	
	/**
	 * 获取分表后的TableName
	 * @param baseTableName 基础表
	 * @param params 传入参数
	 * @return
	 */
	protected String getFinalTable(String baseTableName, Object params, Map<String, Strategy> strategies) {

		Strategy strategy = strategies.get(baseTableName);

		Validate.notBlank(baseTableName);
		if(strategy != null) {
			logger.debug("[currentStrategy ClassName] " + strategy.getClass().getName());
			return strategy.getFinalTable(baseTableName, params);
		}
		return baseTableName;
	}
	
	@Override
	public String convert(Statement statement, Object params, String mapperId, Map<String, Strategy> strategies) {
		if(statement == null) {
			return null;
		}
		return doDeParse(doConvert(statement, params, strategies));
	}
}
