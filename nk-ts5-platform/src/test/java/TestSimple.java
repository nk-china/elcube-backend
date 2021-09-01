import camundajar.impl.scala.Int;
import cn.nkpro.ts5.utils.TextUtils;
import com.alibaba.fastjson.JSON;
import com.apifan.common.random.source.AreaSource;
import com.apifan.common.random.source.NumberSource;
import com.apifan.common.random.source.PersonInfoSource;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSimple {


    LocalDate beginDate = LocalDate.of(1990,1,1);
    LocalDate endDate   = LocalDate.of(2021,12,31);
    @Test
    public void test() throws Exception {
        System.out.println(NumberSource.getInstance().randomInt(100, 10000));
    }
}
