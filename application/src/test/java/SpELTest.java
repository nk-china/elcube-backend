import cn.nkpro.elcube.co.spel.NkSpELManager;
import cn.nkpro.elcube.docengine.gen.DocH;
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
        ctx.setVariable("$doc",docH);


        NkSpELManager nkSpELManager = new NkSpELManager();

        System.out.println(nkSpELManager.invoke("#$doc.docName", ctx));


    }
}
