package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.co.NkComponent;
import org.springframework.expression.EvaluationContext;

import java.util.Map;

public interface NkField extends NkComponent {
    Object process(Object value, Map<String, Object> inputOptions, EvaluationContext context);
}
