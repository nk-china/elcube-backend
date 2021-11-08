package cn.nkpro.ts5.docengine.datasync.defaults;

import cn.nkpro.ts5.exception.NkSystemException;
import cn.nkpro.ts5.docengine.datasync.NkAbstractDocDataAsyncAdapter;
import cn.nkpro.ts5.docengine.gen.NkAsyncQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("NkDocSimpleAsync")
public class NkDocSimpleAsyncImpl extends NkAbstractDocDataAsyncAdapter {

    @Override
    protected void schedule(NkAsyncQueue asyncQueue) {

        log.info(asyncQueue.toString());
        if(Math.random()>0)
            throw new NkSystemException("自定义错误");
    }
}
