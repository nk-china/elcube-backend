package cn.nkpro.ts5.dataengine.meter;

import cn.nkpro.ts5.co.NkCustomObject;

import java.util.Map;

public interface NkMeter<DT> extends NkCustomObject {

    String getName();

    DT getData(Object config);

    default int getW(){
        return 8;
    }

    default int getH(){
        return 6;
    }

    Map<?,?> getVueTemplate();
}
