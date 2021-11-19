package cn.nkpro.ts5.docengine.model;

import cn.nkpro.ts5.docengine.gen.DocDefIWithBLOBs;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocDefIV extends DocDefIWithBLOBs {
    private Object      config;
    private String      dataComponentName;
    private String[]    defComponentNames;
    private String      position;
    // 卡片是否允许编辑，默认真
    private Boolean     writeable = true;
    private boolean     debug;
}
