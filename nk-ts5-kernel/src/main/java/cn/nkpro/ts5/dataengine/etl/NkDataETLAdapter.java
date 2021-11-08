package cn.nkpro.ts5.dataengine.etl;

import cn.nkpro.ts5.co.NkCustomObject;
import cn.nkpro.ts5.dataengine.cards.ReduceConfig;

public interface NkDataETLAdapter extends NkCustomObject {
    void execute(ReduceConfig config);
}
