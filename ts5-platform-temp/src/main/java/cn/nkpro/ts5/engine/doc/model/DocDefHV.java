package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.orm.mb.gen.DocDefFlow;
import cn.nkpro.ts5.orm.mb.gen.DocDefH;
import cn.nkpro.ts5.orm.mb.gen.DocDefState;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DocDefHV extends DocDefH {
    private List<DocDefState> status;
    private List<DocDefFlow> flows;
    private List<DocDefIV> cards;
    private Map<String, String> lifeCycles;
}
