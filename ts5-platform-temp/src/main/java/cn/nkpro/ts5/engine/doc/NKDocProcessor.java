package cn.nkpro.ts5.engine.doc;

import cn.nkpro.ts5.basic.NKCustomObject;
import lombok.Getter;

public interface NKDocProcessor extends NKCustomObject {
    enum EnumDocClassify {
        PARTNER,
        TRANSACTION
    }
    EnumDocClassify classify();
}
