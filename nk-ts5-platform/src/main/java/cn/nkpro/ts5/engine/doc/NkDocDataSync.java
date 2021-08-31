package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.engine.co.NkCustomObject;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.orm.mb.gen.DocDefDataSync;
import org.springframework.expression.EvaluationContext;

import java.util.Map;

public interface NkDocDataSync<K> extends NkCustomObject {

    void run(DocHV doc, DocHV original, EvaluationContext context, EvaluationContext contextOriginal, DocDefDataSync config);

    default void onInsert(Map<K,Map<String,Object>> listMapped, DocDefDataSync config){}
    default void onModify(Map<K,Map<String,Object>> listMapped, DocDefDataSync config){}
    default void onRemove(Map<K,Map<String,Object>> listMapped, DocDefDataSync config){}
}
