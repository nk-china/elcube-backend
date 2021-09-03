package cn.nkpro.ts5.mybatis.breach.converter;

import cn.nkpro.ts5.mybatis.breach.Strategy;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.deparser.SelectDeParser;

import java.util.Map;

/**
 * select语句转换器
 * @author janjan, xujian_jason@163.com
 *
 */
public class SelectSqlConverter extends AbstractSqlConverter {

	@Override
	public Statement doConvert(final Statement statement, final Object params,final Map<String, Strategy> strategies) {
		Select select = (Select) statement;
		SelectDeParser deParser = new SelectDeParser(){
			@Override
			public void visit(Table tableName) {
				tableName.setName(getFinalTable(tableName.getName(), params, strategies));
				super.visit(tableName);
			}
		};
		select.getSelectBody().accept(deParser);
		return statement;
	}
	
}
