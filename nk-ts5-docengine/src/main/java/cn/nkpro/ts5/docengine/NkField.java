package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.co.NkComponent;

import java.util.Map;

public interface NkField extends NkComponent {
    void afterGetDef(Map<String, Object> inputOptions);
}
