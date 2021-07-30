package cn.nkpro.ts5.engine.co;

import cn.nkpro.ts5.engine.doc.model.ScriptDefHV;

public interface NKCustomScriptObject extends NKCustomObject{

    @SuppressWarnings("unused")
    void setScriptDef(ScriptDefHV scriptDef);

    ScriptDefHV getScriptDef();
}
