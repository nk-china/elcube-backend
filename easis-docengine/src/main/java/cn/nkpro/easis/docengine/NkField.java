package cn.nkpro.easis.docengine;

import cn.nkpro.easis.annotation.NkScriptType;
import cn.nkpro.easis.co.NkScriptComponent;
import cn.nkpro.easis.docengine.cards.NkBaseContext;
import cn.nkpro.easis.docengine.cards.NkCalculateContext;
import cn.nkpro.easis.docengine.cards.NkDynamicFormDefI;
import cn.nkpro.easis.co.easy.EasySingle;
import org.springframework.expression.EvaluationContext;
@NkScriptType("Field")
public interface NkField extends NkScriptComponent {

    void beforeCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext);

    void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkBaseContext baseContext);

    void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext);
}
