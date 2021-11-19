package cn.nkpro.ts5.co;

import java.util.Map;

public interface NkComponent extends NkCustomObject {

    String getName();

    Map<?,?> getVueTemplate();
}
