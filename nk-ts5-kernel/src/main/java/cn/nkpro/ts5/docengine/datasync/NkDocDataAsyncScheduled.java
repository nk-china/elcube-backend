package cn.nkpro.ts5.docengine.datasync;

import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.docengine.gen.DocAsyncQueue;
import cn.nkpro.ts5.docengine.gen.DocAsyncQueueExample;
import cn.nkpro.ts5.docengine.gen.DocAsyncQueueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class NkDocDataAsyncScheduled {

    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;

    @Autowired@SuppressWarnings("all")
    private DocAsyncQueueMapper asyncQueueMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void cron(){

        DocAsyncQueueExample example = new DocAsyncQueueExample();
        example.createCriteria()
                .andAsyncStateEqualTo("WAITING");
        example.setOrderByClause("UPDATED_TIME");

        List<DocAsyncQueue> asyncQueues = asyncQueueMapper.selectByExample(example);

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
