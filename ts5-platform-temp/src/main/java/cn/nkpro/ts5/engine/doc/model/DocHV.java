package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.model.mb.gen.DocH;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@Data
public class DocHV extends DocH {

    private DocDefHV def;

    private Map<String,Object> data;

    public DocHV() {
        this.data = new HashMap<>();
    }
    // 单据是否允许编辑，默认真
    private Boolean     writeable = true;
}
