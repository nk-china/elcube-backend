package cn.nkpro.ts5.bigdata.etl;

import cn.nkpro.ts5.bigdata.cards.ReduceConfig;
import cn.nkpro.ts5.co.NkCustomObject;

public interface NkDataETLAdapter extends NkCustomObject {
    void execute(ReduceConfig config);
}
