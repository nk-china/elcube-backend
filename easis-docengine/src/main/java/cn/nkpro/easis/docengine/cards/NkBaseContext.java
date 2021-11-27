package cn.nkpro.easis.docengine.cards;

import cn.nkpro.easis.docengine.model.DocHV;
import lombok.Data;

import java.util.List;

@Data
public class NkBaseContext {
    private DocHV doc;
    private List<? extends NkDynamicFormDefI> fields;
}
