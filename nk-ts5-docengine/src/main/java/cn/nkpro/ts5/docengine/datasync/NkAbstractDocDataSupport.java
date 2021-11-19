package cn.nkpro.ts5.docengine.datasync;

import cn.nkpro.ts5.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.co.spel.NkSpELManager;
import cn.nkpro.ts5.docengine.gen.DocDefDataSync;
import cn.nkpro.ts5.docengine.model.DocHV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;

import java.util.Arrays;

@Slf4j
public abstract class NkAbstractDocDataSupport extends NkAbstractCustomScriptObject {

    @Autowired
    protected NkSpELManager spELManager;

    @SuppressWarnings({"unused"})
    public final void sync(DocHV doc, DocHV original, EvaluationContext context, EvaluationContext contextOriginal, DocDefDataSync config) {

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

        this.doSync(dataUnmapping, dataOriginalUnmapping, context, contextOriginal, config);
    }

    protected abstract void doSync(Object dataUnmapping, Object dataOriginalUnmapping, EvaluationContext context1, EvaluationContext context2, DocDefDataSync def);
}