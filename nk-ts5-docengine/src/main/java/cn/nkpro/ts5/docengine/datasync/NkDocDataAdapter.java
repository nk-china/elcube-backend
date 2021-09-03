package cn.nkpro.ts5.docengine.datasync;

import cn.nkpro.ts5.co.NkCustomObject;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.gen.DocDefDataSync;
import org.springframework.expression.EvaluationContext;

public interface NkDocDataAdapter extends NkCustomObject {

    void sync(DocHV doc, DocHV original, EvaluationContext context, EvaluationContext contextOriginal, DocDefDataSync config);
}
