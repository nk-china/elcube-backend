import cn.nkpro.ts5.utils.DateTimeUtilz;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by bean on 2020/8/4.
 */
public class TestWithoutSpring {

    @Test
    public void test(){

        long a = DateTimeUtilz.fromISO("2021-08-18T16:00:00.000Z");
        System.out.println(new Date(a*1000));

    }
}
