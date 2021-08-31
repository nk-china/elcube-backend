package cn.nkpro.ts5.engine.doc.impl.datasync;

import cn.nkpro.ts5.engine.doc.abstracts.NkAbstractDocDataSimpleSync;
import cn.nkpro.ts5.orm.mb.gen.DocDefDataSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Slf4j
@Component("NkDocRestDataSync")
public class NkDocRestDataSyncImpl extends NkAbstractDocDataSimpleSync {

    @Override
    protected void executeSingle(Map singleData, DocDefDataSync def) {
        log.info(singleData.toString());
    }

    @Override
    protected void executeMultiple(List multipleData, DocDefDataSync def) {
        log.info(multipleData.toString());
    }
}
