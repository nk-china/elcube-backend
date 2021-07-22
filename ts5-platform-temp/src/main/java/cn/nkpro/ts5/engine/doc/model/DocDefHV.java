package cn.nkpro.ts5.engine.doc.model;

import java.util.List;
import java.util.Map;

import cn.nkpro.ts5.model.mb.gen.DefDocStatus;
import cn.nkpro.ts5.model.mb.gen.DocDefH;
import cn.nkpro.ts5.model.mb.gen.DocDefIWithBLOBs;
import lombok.Data;

@Data
public class DocDefHV extends DocDefH {
    private List<DefDocStatus> status;
    private Map<String, String> businessFlows;
    private Map<String, String> lifeCycles;
    private List<DocDefIWithBLOBs> items;
    private Map<String, Object> itemDefs;
}
