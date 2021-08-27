package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.orm.mb.gen.DocH;
import cn.nkpro.ts5.orm.mb.gen.DocI;
import cn.nkpro.ts5.utils.BeanUtilz;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 单据数据对象，缓存到redis的单据数据格式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocHPersistent extends DocH implements Cloneable {

    private Map<String, DocI> items;

    private Map<String,Object> dynamics;

    DocHPersistent() {
        this.items      = new HashMap<>();
        this.dynamics   = new HashMap<>();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DocHPersistent clone = (DocHPersistent) super.clone();
        clone.items    = items;
        clone.dynamics = new HashMap<>(dynamics);
        return clone;
    }
}
