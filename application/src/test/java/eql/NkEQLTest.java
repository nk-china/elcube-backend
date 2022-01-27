package eql;

import cn.nkpro.elcube.ELCubeApplication;
import cn.nkpro.elcube.docengine.NkEqlEngine;
import cn.nkpro.elcube.docengine.model.DocHV;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.update.Update;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={ELCubeApplication.class})
public class NkEQLTest {

    @Autowired
    private NkEqlEngine docEngine;

    @Test
    public void testSelect(){

        /*
         * el 表达式通过 " 或 ` 符号包裹，不建议使用 ' 包裹
         */

//        List<DocHQL> docs = docEngine.findByEql(
//                "select *, docName, data.customer.name, \"data.customer.name\" as name1, `data.customer.name` as name1, 'data.customer.name' as name3, `data.plans?.^[true]?.name` as plan_name, el('data.plans?.^[true]?.name') " +
//                        "from doc where d.abc is null and docId = '11bd25ac-c303-484b-b7c0-c66816e18cf2'");
//
//        System.out.println(docs);


        List<DocHV> updatedDocs = docEngine.execUpdateEql(
                "update doc set docName = '123', \"data.payment.^[true].remark\"='123', createdTime = 0,`partnerName` = null, docName = `data.payment.^[true].expireDate` " +
                        "where docId = '13b24c8a-66aa-4753-8444-b790d8602c7f'"
        );
        System.out.println(updatedDocs);
    }

    @Test
    public void test1() throws JSQLParserException {

        Update update = (Update) CCJSqlParserUtil.parse(
                "update doc set docName = '123', \"data.payment.^[true].remark\"='123', createdTime = 0,`partnerName` = null,a=\"true\" " +
                "where docId = '13b24c8a-66aa-4753-8444-b790d8602c7f'");


        update.getUpdateSets().stream()
                .forEach(updateSet -> {
                    String name = updateSet.getColumns().get(0).getName(false);
                    String value = updateSet.getExpressions().get(0).toString();

                    System.out.println(name);
                    System.out.println(value);
                    System.out.println();
                });


        //docEngine.doUpdateByEql("update doc set docName = '123', data.payment.^[true].remark='123' where docId = '13b24c8a-66aa-4753-8444-b790d8602c7f'","test");

//        docEngine.doUpdateByEql(
//                "update doc set docName = '123', base.name = base.name " +
//                    "\nwhere dynamic.key_keyword = 123 " +
//                    "\n  and businessKey = 'k' " +
//                    "\n  and businessKey is null" +
//                    "\n  and businessKey is not null" +
//                    "\n  and businessKey > 1" +
//                    "\n  and businessKey >=1 " +
//                    "\n  and businessKey <=1 " +
//                    "\n  and businessKey < 1 " +
//                    "\n  and businessKey <> 1 " +
//                    "\n  and businessKey != 1 " +
//                    "\n  and businessKey != 0x1 " +
//                    "\n  and businessKey like '%abc%' " +
//                    "\n  and businessKey between 2 and 3 " +
//                    "\n  and businessKey in (1,2,3) " +
//                    "\n  and createDate = '2020-01-01' ",
//                "测试"
//        );
//
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
}
