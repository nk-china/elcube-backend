package cn.nkpro.ts5.engine.doc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 单据数据对象，与前端交互的数据格式
 */
@EqualsAndHashCode
@Data
public class DocHV extends DocHD {

    private String docTypeDesc;

    private String docStateDesc;

    private DocDefHV def;

    private Map<String,Object> data;

    private Map<String,Object> dynamics;

    public DocHV() {
        super();
        this.data       = new HashMap<>();
        this.dynamics   = new HashMap<>();
    }
    // 单据是否允许编辑，默认真
    private Boolean     writeable = true;
}
