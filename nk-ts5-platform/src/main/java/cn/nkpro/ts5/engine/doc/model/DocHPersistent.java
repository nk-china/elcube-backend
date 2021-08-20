package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.orm.mb.gen.DocH;
import cn.nkpro.ts5.orm.mb.gen.DocI;
import cn.nkpro.ts5.utils.BeanUtilz;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 单据数据对象，缓存到redis的单据数据格式
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DocHPersistent extends DocH implements Cloneable {

    private Map<String, DocI> items;

    DocHPersistent() {
        this.items = new HashMap<>();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DocHPersistent clone = (DocHPersistent) super.clone();
        clone.setItems(items);
        return clone;
    }
}
