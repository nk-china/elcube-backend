package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.orm.mb.gen.DocDefFlow;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class DocDefFlowV extends DocDefFlow {
    private boolean visible;
    private String visibleDesc;
}
