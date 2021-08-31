package cn.nkpro.ts5.engine.doc.abstracts;

import cn.nkpro.ts5.engine.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.engine.doc.NkDocDataSync;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.spel.TfmsSpELManager;
import cn.nkpro.ts5.orm.mb.gen.DocDefDataSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"all"})
@Slf4j
public abstract class NkAbstractDocDataSync<K> extends NkAbstractCustomScriptObject implements NkDocDataSync<K> {

    @Autowired
    protected TfmsSpELManager spELManager;

    @Override
    public final void run(DocHV doc, DocHV original, EvaluationContext context, EvaluationContext contextOriginal, DocDefDataSync config) {

        // data1 新数据
        Object dataUnmapping = spELManager.invoke(config.getDataSpEL(), context);
        // data2 原数据
        Object dataOriginalUnmapping = original!=null? spELManager.invoke(config.getDataSpEL(), original) :null;

        // 数组需要转换成List
        if(dataUnmapping!=null && dataUnmapping.getClass().isArray()){
            dataUnmapping = Arrays.asList((Object[])dataUnmapping);
        }
        if(dataOriginalUnmapping!=null && dataOriginalUnmapping.getClass().isArray()){
            dataOriginalUnmapping = Arrays.asList((Object[])dataOriginalUnmapping);
        }

        this.execute(dataUnmapping, dataOriginalUnmapping, context, contextOriginal, config);
    }

    protected abstract void execute(Object dataUnmapping, Object dataOriginalUnmapping, EvaluationContext context1, EvaluationContext context2, DocDefDataSync def);
}