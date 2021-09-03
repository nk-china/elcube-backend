package cn.nkpro.ts5.mybatis.breach.converter;

import java.util.Iterator;
import java.util.Map;

import cn.nkpro.ts5.mybatis.breach.Strategy;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

/**
 * update语句转换器
 * @author janjan, xujian_jason@163.com
 *
 */
public class UpdateSqlConverter extends AbstractSqlConverter {

	@Override
	public Statement doConvert(final Statement statement, final Object params,final Map<String, Strategy> strategies) {
		Update update = (Update) statement;
		Iterator<Table> iterator = update.getTables().iterator();
		while(iterator.hasNext()) {
			Table table = iterator.next();
			String baseTableName = table.getName();
			table.setName(super.getFinalTable(baseTableName, params, strategies));
		}
		return statement;
	}
	
}
