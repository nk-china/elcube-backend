package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.orm.mb.gen.DocDefBpm;
import cn.nkpro.ts5.orm.mb.gen.DocDefCycle;
import cn.nkpro.ts5.orm.mb.gen.DocDefH;
import cn.nkpro.ts5.orm.mb.gen.DocDefState;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DocDefHV extends DocDefH {
    private List<DocDefStateV> status;
    private List<DocDefFlowV> flows;
    private List<DocDefFlowV> nextFlows;
    private List<DocDefBpm> bpms;
    private List<DocDefIV> cards;
    private List<DocDefCycle> lifeCycles;
    private boolean debug;
}
