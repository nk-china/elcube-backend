package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.model.mb.gen.DocDefH;
import cn.nkpro.ts5.model.mb.gen.DocDefIWithBLOBs;
import cn.nkpro.ts5.model.mb.gen.DocDefState;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DocDefHV extends DocDefH {
    private List<DocDefState> status;
    private Map<String, String> businessFlows;
    private Map<String, String> lifeCycles;
    private List<DocDefIV> cards;
    private Map<String, Object> itemDefs;
}
