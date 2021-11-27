package cn.nkpro.easis.co;

import java.util.Map;

public interface NkScriptComponent extends NkCustomScriptObject {

    String getName();

    Map<?,?> getVueTemplate();
}
