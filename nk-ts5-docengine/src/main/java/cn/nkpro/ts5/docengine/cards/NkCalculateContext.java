package cn.nkpro.ts5.docengine.cards;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NkCalculateContext extends NkBaseContext {
    private List<String> skip;
    private Map options;
    private boolean isTrigger;
    private boolean isFieldTrigger;
    private Map<String,Object> original;
}
