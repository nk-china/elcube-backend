package cn.nkpro.ts5.mybatis.breach.converter;

import cn.nkpro.ts5.mybatis.breach.Strategy;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;

import java.util.Map;

/**
 * delete语句转换器
 * @author janjan, xujian_jason@163.com
 *
 */
public class DeleteSqlConverter extends AbstractSqlConverter {

	@Override
	public Statement doConvert(final Statement statement, final Object params,final Map<String, Strategy> strategies) {
		Delete delete = (Delete) statement;
		Table table = delete.getTable();
		String baseTableName = table.getName();
		table.setName(super.getFinalTable(baseTableName, params, strategies));
		return statement;
	}
	
}
