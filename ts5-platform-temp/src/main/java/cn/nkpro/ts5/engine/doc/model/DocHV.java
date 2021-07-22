package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.model.mb.gen.DocH;
import lombok.Data;

import java.util.Map;

@Data
public class DocHV extends DocH {

    private DocDefHV def;

    private Map<String,Object> data;
}
