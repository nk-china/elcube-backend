package cn.nkpro.ts5.engine.doc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 单据数据对象，基础格式
 */
@EqualsAndHashCode(callSuper = false)
@Data
class DocHBasis extends DocHPersistent {

    private String docTypeDesc;

    private String docStateDesc;

    private DocDefHV def;

    private Map<String,Object> data;

    private Map<String,Object> dynamics;

    DocHBasis() {
        super();
        this.data       = new HashMap<>();
        this.dynamics   = new HashMap<>();
    }
}
