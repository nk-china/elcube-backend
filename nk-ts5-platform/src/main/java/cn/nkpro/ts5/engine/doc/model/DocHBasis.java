package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.utils.BeanUtilz;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 单据数据对象，基础格式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocHBasis extends DocHPersistent implements Cloneable {

    private String docTypeDesc;

    private String docStateDesc;

    private DocDefHV def;

    private Map<String,Object> data;

    DocHBasis() {
        super();
        this.data       = new HashMap<>();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DocHBasis clone = (DocHBasis) super.clone();
        clone.setDocTypeDesc(docTypeDesc);
        clone.setDocStateDesc(docStateDesc);
        clone.setDef(def);
        clone.data     = new HashMap<>();
        data.forEach((k,v)-> clone.data.put(k,BeanUtilz.cloneWithFastjson(v)));

        return clone;
    }
}
