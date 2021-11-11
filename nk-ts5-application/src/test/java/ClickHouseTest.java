import cn.nkpro.ts5.TS5Application;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.co.remote.NkRemoteAdapter;
import cn.nkpro.ts5.dataengine.service.ClickHouseService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by bean on 2020/7/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TS5Application.class})
public class ClickHouseTest implements ApplicationContextAware {

    @Autowired
    ClickHouseService service;

    @Test
    public void test1() {

        System.out.println(service.get());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println(applicationContext);
    }
}
