package cn.nkpro.easis.co.meter;

import cn.nkpro.easis.annotation.NkScriptType;
import cn.nkpro.easis.co.NkScriptComponent;


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
