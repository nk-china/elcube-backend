package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.annotation.NkScriptType;
import cn.nkpro.ts5.co.NkScriptComponent;
import cn.nkpro.ts5.docengine.cards.NkDynamicFormDefI;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.easy.EasySingle;
import org.springframework.expression.EvaluationContext;

@NkScriptType("Field")
public interface NkField extends NkScriptComponent {

    default void beforeCalculate(NkDynamicFormDefI field, EvaluationContext context, DocHV doc, EasySingle card){}

    default void processOptions(NkDynamicFormDefI field, EvaluationContext context, DocHV doc, EasySingle car){}

    default void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, DocHV doc, EasySingle card){}
}
