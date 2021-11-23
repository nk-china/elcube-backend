package cn.nkpro.ts5.docengine.cards;

import cn.nkpro.ts5.docengine.model.DocHV;
import lombok.Data;

import java.util.Map;

@Data
public class NkDynamicCalculateContext {
    private DocHV doc;
    private boolean isTrigger;
    private boolean isFieldTrigger;
    private Map<String,Object> original;
}
