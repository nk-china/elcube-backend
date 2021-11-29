import cn.nkpro.easis.EasisApplication;
import cn.nkpro.easis.data.redis.RedisSupport;
import cn.nkpro.easis.docengine.service.SequenceSupport;
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
@SpringBootTest(classes={EasisApplication.class})
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
