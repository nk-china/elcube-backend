package cn.nkpro.easis.docengine.datasync.defaults;

import cn.nkpro.easis.docengine.datasync.NkAbstractDocDataAsyncAdapter;
import cn.nkpro.easis.docengine.gen.DocAsyncQueue;
import cn.nkpro.easis.exception.NkSystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("NkDocSimpleAsync")
public class NkDocSimpleAsyncImpl extends NkAbstractDocDataAsyncAdapter {

    @Override
    protected void schedule(DocAsyncQueue asyncQueue) {

        log.info(asyncQueue.toString());
        if(Math.random()>0)
            throw new NkSystemException("自定义错误");
    }
}
