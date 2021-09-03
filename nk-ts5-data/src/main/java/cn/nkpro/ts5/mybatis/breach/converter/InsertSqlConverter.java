package cn.nkpro.ts5.mybatis.breach.converter;

import cn.nkpro.ts5.mybatis.breach.Strategy;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;

import java.util.Map;

/**
 * insert into转换器
 * @author janjan, xujian_jason@163.com
 *
 */
public class InsertSqlConverter extends AbstractSqlConverter {

	@Override
	public Statement doConvert(final Statement statement, final Object params,final Map<String, Strategy> strategies) {
		Insert insert = (Insert) statement;
		Table table = insert.getTable();
		String baseTableName = table.getName();
		table.setName(super.getFinalTable(baseTableName, params, strategies));
		return statement;
	}
	
}
