package cn.nkpro.ts5.co.meter;

import cn.nkpro.ts5.co.NkComponent;
import cn.nkpro.ts5.co.NkCustomObject;

import java.util.Map;

public interface NkMeter<DT> extends NkComponent {

    DT getData(Object config);

    default int getW(){
        return 8;
    }

    default int getH(){
        return 6;
    }
}
