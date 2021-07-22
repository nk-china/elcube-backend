package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.model.mb.gen.DocDefIWithBLOBs;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class DocDefIV extends DocDefIWithBLOBs {
    private Object      config;
    private String      dataComponentName;
    private String[]    defComponentNames;
}
