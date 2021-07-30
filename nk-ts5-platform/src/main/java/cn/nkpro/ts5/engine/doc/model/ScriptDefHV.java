package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.orm.mb.gen.ScriptDefHWithBLOBs;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class ScriptDefHV extends ScriptDefHWithBLOBs {
    private boolean debug;
}
