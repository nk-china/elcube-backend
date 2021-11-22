package cn.nkpro.ts5.co;

import java.util.Map;

public interface NkScriptComponent extends NkCustomScriptObject {

    String getName();

    Map<?,?> getVueTemplate();
}
