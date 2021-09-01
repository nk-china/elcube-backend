package cn.nkpro.ts5.engine.doc.datasync.defaults;

import cn.nkpro.ts5.engine.doc.datasync.NkAbstractDocDataAsyncAdapter;
import cn.nkpro.ts5.exception.TfmsSystemException;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("NkDocSimpleAsync")
public class NkDocSimpleAsyncImpl extends NkAbstractDocDataAsyncAdapter<String> {

    @Override
    protected void schedule(NkAsyncQueue asyncQueue) {
        log.info(asyncQueue.toString());
        if(Math.random()>0)
            throw new TfmsSystemException("自定义错误");
    }
}
