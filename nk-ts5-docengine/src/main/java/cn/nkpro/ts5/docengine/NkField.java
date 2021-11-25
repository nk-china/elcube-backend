package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.annotation.NkScriptType;
import cn.nkpro.ts5.co.NkScriptComponent;
import cn.nkpro.ts5.docengine.cards.NkBaseContext;
import cn.nkpro.ts5.docengine.cards.NkCalculateContext;
import cn.nkpro.ts5.docengine.cards.NkDynamicFormDefI;
import cn.nkpro.ts5.docengine.model.easy.EasySingle;
import org.springframework.expression.EvaluationContext;
@NkScriptType("Field")
public interface NkField extends NkScriptComponent {

    void beforeCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext);

    void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkBaseContext baseContext);

    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext);
}
