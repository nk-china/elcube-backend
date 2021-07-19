import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSimple {

    private static final Pattern pattern = Pattern.compile("\\$\\{(?:[^\"'}]|\"[^\"]*\"|'[^']*'|\\{\\{|}})*}");
    @Test
    public void test(){

        System.out.println("\\$\\{(?:[^\"'}]|\"[^\"]*\"|'[^']*'|\\\\{|\\})*}");

        Matcher matcher = pattern.matcher("${#json.stringify(componentsData['nk-card-doc-partner']?{{}}.?[type=='P001'].![partnerRoleId])}${#json.stringify(componentsData['nk-card-doc-partner']?{{}}.?[type=='P001'].![partnerRoleId])}");


        while (matcher.find()){
            System.out.println(matcher.group(0).replaceAll("\\{\\{","{").replaceAll("}}","}"));
        }
//        ExpressionParser parser = new SpelExpressionParser();
//
//        BizDoc doc = new BizDoc();
//        doc.setDocName("test");
//
//
//        EvaluationContext context = new StandardEvaluationContext(doc);
//        context.setVariable("a",123);
//
//        System.out.println(parser.parseExpression("docName").getValue(context));
//        System.out.println(parser.parseExpression("#a").getValue(context));
//        System.out.println(parser.parseExpression("{1,2,3}.contains(1)").getValue(context));


    }
}
