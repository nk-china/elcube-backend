package cn.nkpro.ts5.co.meter;

import cn.nkpro.ts5.annotation.NkScriptType;
import cn.nkpro.ts5.co.NkScriptComponent;


@NkScriptType("Meter")
public interface NkMeter<DT> extends NkScriptComponent {

    DT getData(Object config);

    default int getW(){
        return 8;
    }

    default int getH(){
        return 6;
    }
}
