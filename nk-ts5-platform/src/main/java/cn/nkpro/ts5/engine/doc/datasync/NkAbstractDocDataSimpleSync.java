package cn.nkpro.ts5.engine.doc.datasync;

import cn.nkpro.ts5.engine.spel.TfmsSpELManager;
import cn.nkpro.ts5.orm.mb.gen.DocDefDataSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"all"})
@Slf4j
public abstract class NkAbstractDocDataSimpleSync<K> extends NkAbstractDocDataSync<K> {

    @Autowired
    protected TfmsSpELManager spELManager;

    protected void execute(Object dataUnmapping, Object dataOriginalUnmapping, EvaluationContext context1, EvaluationContext context2, DocDefDataSync def){
        if(dataUnmapping instanceof List){
            executeMultiple((List<Map<String, Object>>) ((List) dataUnmapping).stream()
                    .map(value -> {
                        context1.setVariable("row", value);
                        return (Map<String, Object>) (spELManager.invoke(def.getMappingSpEL(), context1));
                    }).collect(Collectors.toList()), def);
        }else{
            context1.setVariable("row", dataUnmapping);
            executeSingle((Map)(spELManager.invoke(def.getMappingSpEL(), context1)), def);
        }
    }

    protected abstract void executeSingle(Map<String,Object> singleData, DocDefDataSync def);

    protected abstract void executeMultiple(List<Map<String,Object>> multipleData, DocDefDataSync def);
}