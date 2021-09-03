import com.apifan.common.random.source.NumberSource;
import org.junit.Test;

import java.time.LocalDate;

public class TestSimple {


    LocalDate beginDate = LocalDate.of(1990,1,1);
    LocalDate endDate   = LocalDate.of(2021,12,31);
    @Test
    public void test() throws Exception {
        System.out.println(NumberSource.getInstance().randomInt(100, 10000));
    }
}
