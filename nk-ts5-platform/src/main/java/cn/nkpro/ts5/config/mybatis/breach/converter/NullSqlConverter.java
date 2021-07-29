package cn.nkpro.ts5.config.mybatis.breach.converter;

import cn.nkpro.ts5.config.mybatis.breach.Strategy;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;

/**
 * 空转换器
 * @author janjan, xujian_jason@163.com
 *
 */
public class NullSqlConverter extends AbstractSqlConverter {

	@Override
	public Statement doConvert(final Statement statement, final Object params,final Map<String, Strategy> strategies) {
		return statement;
	}
	
}
