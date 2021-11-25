package cn.nkpro.ts5.docengine.cards;

import cn.nkpro.ts5.docengine.model.DocHV;
import lombok.Data;

import java.util.List;

@Data
public class NkBaseContext {
    private DocHV doc;
    private List<? extends NkDynamicFormDefI> fields;
}
