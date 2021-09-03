package cn.nkpro.ts5.docengine.datasync;

import cn.nkpro.ts5.docengine.gen.DocDefDataSync;
import cn.nkpro.ts5.spel.NkSpELManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"all"})
@Slf4j
public abstract class NkAbstractDocDataGeneralSyncAdapter<K> extends NkAbstractDocDataSupport implements NkDocDataSyncAdapter<K> {

    @Autowired
    protected NkSpELManager spELManager;

    protected void doSync(Object dataUnmapping, Object dataOriginalUnmapping, EvaluationContext context1, EvaluationContext context2, DocDefDataSync def){
        if(dataUnmapping instanceof List){
            doSyncMultiple((List<Map<String, Object>>) ((List) dataUnmapping).stream()
                    .map(value -> {
                        context1.setVariable("row", value);
                        return (Map<String, Object>) (spELManager.invoke(def.getMappingSpEL(), context1));
                    }).collect(Collectors.toList()), def);
        }else{
            context1.setVariable("row", dataUnmapping);
            doSyncSingle((Map)(spELManager.invoke(def.getMappingSpEL(), context1)), def);
        }
    }

    protected abstract void doSyncSingle(Map<String,Object> singleData, DocDefDataSync def);

    protected abstract void doSyncMultiple(List<Map<String,Object>> multipleData, DocDefDataSync def);
}