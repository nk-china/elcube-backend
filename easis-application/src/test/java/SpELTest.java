import cn.nkpro.easis.co.spel.NkSpELManager;
import cn.nkpro.easis.docengine.gen.DocH;
import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bean on 2020/7/22.
 */
public class SpELTest {

    private static final ExpressionParser parser = new SpelExpressionParser();

    @Test
    public void test2(){

        //Pattern compile = Pattern.compile("\\$\\{(?:\"[^\"]*\"|'[^']*'|\\{[^}]*}|[^\"'}])*+}");
        Pattern compile = Pattern.compile("\"?\\$\\{((?:\"[^\"]*\"|'[^']*'|\\{[^}]*\\{[^}]*}[^}]*}|\\{[^}]*}|[^\"'}])*?)}\"?");
//        Pattern compile = Pattern.compile("(\\$\\{(\\{[^}]*?}|[^}])*})*");


//        m(compile,"${ab{123c}");
//        m(compile,"${ab123c}");
//        m(compile,"${ab{123c}}");
        m(compile,"\"${ab{1\"23c}12{{}}3}\"a\"bc${abc}");
        m(compile,"${ab}abc${abc}");
        //System.out.println(matcher.group());

    }

    private void m(Pattern compile, String input){
        System.out.println();
        System.out.println(input);
        Matcher matcher = compile.matcher(input);
        while (matcher.find()){
            System.out.println(matcher.groupCount());
            System.out.println(matcher.group(0));
            System.out.println(matcher.group(1));
        }
    }

    @Test
    public void test1() throws Exception {

        DocH docH = new DocH();
        docH.setDocName("单据名称");

        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setVariable("doc",docH);


        NkSpELManager nkSpELManager = new NkSpELManager();

        long a = System.currentTimeMillis();
        for(int i=0;i<1000000;i++){
            nkSpELManager.convert("{" +
                    "\"name\":\"${docName}\"" +
                    "" +
                    "}", docH);
        }
//        System.out.println(nkSpELManager.convert("{" +
//                "\"name\":\"${#doc}\"" +
////                "\"prop\":${1 between {1, 2}}" +
//                "}", ctx));
//
        System.out.println(System.currentTimeMillis()-a);
////
//
//        System.out.println(parser.parseExpression("{" +
//                "\"name\":\"#{#doc.docName}\"" +
//                "\"prop\":\"#{1 between {1, 2}}\"" +
//                "}", new TemplateParserContext("\"#{","}\""))
//                .getValue(ctx));


        long b = System.currentTimeMillis();
        for(int i=0;i<1000000;i++){
            parser.parseExpression("{" +
                    "\"name\":\"#{docName}\"" +
                    "" +
                    "}", new TemplateParserContext("\"#{","}\""))
                    .getValue(docH);
        }
        System.out.println(System.currentTimeMillis()-b);

    }
}
