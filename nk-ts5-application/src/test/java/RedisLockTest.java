import cn.nkpro.ts5.TS5Application;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.co.remote.NkRemoteAdapter;
import cn.nkpro.ts5.data.redis.RedisSupport;
import cn.nkpro.ts5.docengine.service.SequenceSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by bean on 2020/7/22.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={TS5Application.class})
public class RedisLockTest {

    @Autowired
    RedisSupport<Long> redisSupport;

    @Autowired
    SequenceSupport sequenceSupport;
    @Autowired
    @Qualifier("nkTaskExecutor")
    private TaskExecutor taskExecutor;

    @Test
    public void test1() throws Exception {

        redisSupport.delete("CACHE_SYS_SEQUENCE:DOC");

        for(int i=0;i<100000;i++){
            taskExecutor.execute(() -> {
                System.out.println(sequenceSupport.next(null,null));
            });
        }

        Thread.sleep(1000 * 20);

    }
}
