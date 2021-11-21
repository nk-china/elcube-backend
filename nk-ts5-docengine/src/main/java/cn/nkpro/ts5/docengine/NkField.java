package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.co.NkComponent;
import cn.nkpro.ts5.docengine.cards.NkDynamicFormDefI;
import cn.nkpro.ts5.docengine.model.DocHV;
import cn.nkpro.ts5.docengine.model.easy.EasySingle;
import org.springframework.expression.EvaluationContext;

public interface NkField extends NkComponent {

    default void beforeCalculate(NkDynamicFormDefI field, EvaluationContext context, DocHV doc, EasySingle card){}

    default void processOptions(NkDynamicFormDefI field, EvaluationContext context, DocHV doc, EasySingle car){}

    default void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, DocHV doc, EasySingle card){}
}
