package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.orm.mb.gen.DocDefState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DocDefStateV extends DocDefState {
    private boolean visible;
}
