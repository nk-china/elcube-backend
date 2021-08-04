package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.engine.task.model.BpmTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 单据数据对象，与前端交互的数据格式
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class DocHV extends DocHD {

    private String docTypeDesc;

    private String docStateDesc;

    private DocDefHV def;

    private Map<String,Object> data;

    private Map<String,Object> dynamics;
    // 单据是否允许编辑，默认真
    private Boolean     writeable = true;

    private BpmTask bpmTask;

    public DocHV() {
        super();
        this.data       = new HashMap<>();
        this.dynamics   = new HashMap<>();
    }
}
