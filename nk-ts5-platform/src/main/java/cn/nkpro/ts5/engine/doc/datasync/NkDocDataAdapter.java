package cn.nkpro.ts5.engine.doc.datasync;

import cn.nkpro.ts5.engine.co.NkCustomObject;
import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.orm.mb.gen.DocDefDataSync;
import cn.nkpro.ts5.orm.mb.gen.NkAsyncQueue;
import org.springframework.expression.EvaluationContext;

public interface NkDocDataAdapter extends NkCustomObject {

    void sync(DocHV doc, DocHV original, EvaluationContext context, EvaluationContext contextOriginal, DocDefDataSync config);
}
