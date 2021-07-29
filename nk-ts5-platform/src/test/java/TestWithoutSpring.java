import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;

/**
 * Created by bean on 2020/8/4.
 */
public class TestWithoutSpring {

    @Test
    public void test(){
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("#a * #c");

        StandardEvaluationContext ctx = new StandardEvaluationContext(new HashMap(){{
            put("b",1);
        }});
        //ctx.setRootObject(doc);
        ctx.setVariable("a",2);
        ctx.setVariable("c",3);
        //ctx.setVariable("list", list);

        Object value = exp.getValue(ctx);
        System.out.println(value);
    }
}
