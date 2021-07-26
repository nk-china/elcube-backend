package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.model.mb.gen.DocH;
import cn.nkpro.ts5.model.mb.gen.DocI;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 单据数据对象，缓存到redis的单据数据格式
 */
@EqualsAndHashCode
@Data
public class DocHD extends DocH {

    private Map<String, DocI> items;

    DocHD() {
        this.items = new HashMap<>();
    }
}
