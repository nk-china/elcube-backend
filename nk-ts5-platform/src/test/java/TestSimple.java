import camundajar.impl.scala.Int;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestSimple {


    @Test
    public void test() throws Exception {
//        Arrays.stream(
//                A.class.getDeclaredMethods()
//        ).forEach(System.out::println);
        Method runB = B.class.getMethod("run");
        Method runC = C.class.getMethod("run");

        System.out.println(runB.getDeclaringClass()==A.class);
        System.out.println(runC.getDeclaringClass()==A.class);


        System.out.println(Class.forName("java.lang.Void").cast(null));
        System.out.println(Class.forName("java.lang.Integer").cast(1));


        System.out.println(JSON.parseObject("1", Integer.class));
        System.out.println(JSON.parseObject("1", String.class));

        System.out.println(JSON.toJSONString(null));
        System.out.println(JSON.parseObject("null", Void.class)==null);

    }
}

interface A{
    default void run(){

    }
}
class B implements A{
}
class C extends B{
    @Override
    public void run() {

    }
}