import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.service.impl.NkDocEngineServiceImpl;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.util.AddAliasesVisitor;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.junit.Test;

public class SqlParserTest{


    @Test
    public void test1() throws JSQLParserException {


        NkDocEngine docEngine = new NkDocEngineServiceImpl();


        docEngine.doUpdateByEql(
                "update ZR01 set docName = '123', base.name = base.name " +
                    "\nwhere dynamic.key_keyword = 123 " +
                    "\n  and businessKey = 'k' " +
                    "\n  and businessKey is null" +
                    "\n  and businessKey is not null" +
                    "\n  and businessKey > 1" +
                    "\n  and businessKey >=1 " +
                    "\n  and businessKey <=1 " +
                    "\n  and businessKey < 1 " +
                    "\n  and businessKey <> 1 " +
                    "\n  and businessKey != 1 " +
                    "\n  and businessKey != 0x1 " +
                    "\n  and businessKey like '%abc%' " +
                    "\n  and businessKey between 2 and 3 " +
                    "\n  and businessKey in (1,2,3) " +
                    "\n  and createDate = '2020-01-01' ",
                "测试"
        );

//        Statement statement = CCJSqlParserUtil.parse(
//                "update ZR01 a set docName = '123', base.name = base.name where dynamic.key_keyword = 123 and businessKey = 'k' and businessKey = 'k'");
//
//        if(statement instanceof Update){
//            Update update = (Update) statement;
//
////            System.out.println(update.getTable().getName());
//
////            update.getUpdateSets().forEach(updateSet -> {
////                System.out.println(updateSet.getColumns().get(0).getFullyQualifiedName());
////                System.out.println(updateSet.getColumns().get(0).getName(false));
////                System.out.println(updateSet.getExpressions().get(0));
////            });
//
//            update.getWhere().accept(new NkEqlExpressionVisitor());
//
//
//        }


    }

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
