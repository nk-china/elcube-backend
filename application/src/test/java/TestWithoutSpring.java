import cn.nkpro.elcube.utils.DateTimeUtilz;
import org.junit.Test;

import java.util.*;

/**
 * Created by bean on 2020/8/4.
 */
public class TestWithoutSpring {

    @Test
    public void test(){

        long a = DateTimeUtilz.fromISO("2021-08-18T16:00:00.000Z");
        System.out.println(new Date(a*1000));

        Map<String,Object> map1 = new HashMap<>();
        map1.put("1","1");
        map1.put("4","4");
        map1.put("2","2");
        map1.put("a","a");
        map1.put("5","5");
        map1.put("3","3");
        map1.put("6","6");
        map1.put("b","b");

        System.out.println(map1.keySet());
        System.out.println(map1.values());

    }
}
