import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.AddAliasesVisitor;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.junit.Test;

import java.util.stream.Collectors;

public class SqlParserTest{

    @Test
    public void test(){

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        final AddAliasesVisitor addAliasesVisitor = new AddAliasesVisitor();

        try {
            Select select = (Select) CCJSqlParserUtil.parse(
                    "SELECT \"dynamics.department_name.original\" AS \"____dynamics.department_name.original\", COUNT(classify) AS \"____classify\", ROUND(MAX(\"dynamics.lat_double\"),2) AS \"____dynamics.lat_double\", AVG(\"dynamics.num1_int\") AS \"____dynamics.num1_int\", FIRST(\"dynamics.phone_serial.original\") AS \"____dynamics.phone_serial.original\" FROM \"document-custom\"  WHERE \"dynamics.company_name.original\" IS NOT NULL and a='asdf'  GROUP BY \"____dynamics.department_name.original\"    "
            );

            PlainSelect selectBody = (PlainSelect) select.getSelectBody();


            Expression expression = CCJSqlParserUtil.parseExpression("HISTOGRAM(dynamics.date1_date,INTERVAL    1 MONTH)");
            System.out.println(expression);

            SelectExpressionItem item = new SelectExpressionItem();
            item.withExpression(new Column("HISTOGRAM(dynamics.date1_date,INTERVAL 1 MONTH)"));
            selectBody.addSelectItems(item);

            System.out.println(selectBody);

//
//            System.out.println("select");
//            System.out.println(selectBody.getSelectItems().stream().map(Object::toString).collect(Collectors.joining(",")));
//            System.out.println("from");
//            System.out.println(tablesNamesFinder.getTableList(select).stream().findFirst().orElse(null));
//            System.out.println(selectBody.getWhere());
//            System.out.println(selectBody.getIntoTables());
//
//            System.out.println(selectBody.getFetch());

//            System.out.println(tablesNamesFinder.getTableList(select).stream().findFirst().orElse(null));
//
//            System.out.println(select.getSelectBody());
//            System.out.println(select.getWithItemsList());

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }
}
