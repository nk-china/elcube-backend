package cn.nkpro.ts5.engine.doc.datasync;

import cn.nkpro.ts5.engine.co.NkCustomObjectManager;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueue;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueueExample;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class NkDocDataAsyncScheduled {

    @Autowired
    private NkCustomObjectManager customObjectManager;

    @Autowired
    private NkAsyncQueueMapper asyncQueueMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void cron(){

        NkAsyncQueueExample example = new NkAsyncQueueExample();
        example.createCriteria()
                .andAsyncStateEqualTo("WAITING");
        example.setOrderByClause("UPDATED_TIME");

        List<NkAsyncQueue> asyncQueues = asyncQueueMapper.selectByExample(example);

        if(log.isInfoEnabled())
            log.info("数据同步重试 任务数量：{}",asyncQueues.size());

        asyncQueues.forEach(asyncQueue->
            customObjectManager.getCustomObject(asyncQueue.getAsyncObjectRef(), NkDocDataAsyncAdapter.class)
                .run(asyncQueue)
        );

        if(log.isInfoEnabled())
            log.info("数据同步重试完成 任务数量：{}",asyncQueues.size());
    }
}
