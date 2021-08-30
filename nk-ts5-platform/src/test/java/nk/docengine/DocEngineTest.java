package nk.docengine;

import cn.nkpro.ts5.TS5PlatformApplication;
import cn.nkpro.ts5.config.redis.RedisSupport;
import cn.nkpro.ts5.engine.doc.impl.NkDocEngineServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by bean on 2020/7/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TS5PlatformApplication.class})
public class DocEngineTest {
    @Autowired
    private RedisSupport<String> redisSupport;

    @Test
    public void test1() throws Exception {

        Map<String,String> map = redisSupport.getHashIfAbsent("bean",true, HashMap::new);

        System.out.println(map);
        System.out.println(redisSupport.getHash("bean"));

    }
}
