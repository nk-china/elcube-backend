package cn.nkpro.ts5.engine.doc.datasync;

import cn.nkpro.ts5.engine.co.NkCustomObjectManager;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueueExample;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

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
        asyncQueueMapper.selectByExample(example).forEach(asyncQueue->{
            customObjectManager.getCustomObject(asyncQueue.getAsyncObjectRef(), NkAbstractDocDataAsyncAdapter.class)
                    .schedule(asyncQueue);
        });
    }
}
