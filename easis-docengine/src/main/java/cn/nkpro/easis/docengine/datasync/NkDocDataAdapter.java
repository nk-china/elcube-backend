package cn.nkpro.easis.docengine.datasync;

import cn.nkpro.easis.co.NkCustomObject;
import cn.nkpro.easis.docengine.gen.DocDefDataSync;
import cn.nkpro.easis.docengine.model.DocHV;
import org.springframework.expression.EvaluationContext;

public interface NkDocDataAdapter extends NkCustomObject {

    void sync(DocHV doc, DocHV original, EvaluationContext context, EvaluationContext contextOriginal, DocDefDataSync config);
}
