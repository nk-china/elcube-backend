package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.orm.mb.gen.DocDefIWithBLOBs;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class DocDefIV extends DocDefIWithBLOBs {
    private Object      config;
    private String      dataComponentName;
    private String[]    defComponentNames;
    private String      position;
    // 卡片是否允许编辑，默认真
    private Boolean     writeable = true;
}
