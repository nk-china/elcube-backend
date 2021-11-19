package cn.nkpro.ts5.docengine.model;

import cn.nkpro.ts5.co.DebugAble;
import cn.nkpro.ts5.docengine.gen.DocH;
import cn.nkpro.ts5.docengine.gen.DocI;
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
public class DocHPersistent extends DocH implements Cloneable, DebugAble {

    private Map<String, DocI> items;

    private Map<String,Object> dynamics;

    private boolean debug;

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
