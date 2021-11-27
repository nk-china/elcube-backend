package cn.nkpro.easis.bigdata.etl;

import cn.nkpro.easis.bigdata.cards.ReduceConfig;
import cn.nkpro.easis.co.NkCustomObject;

public interface NkDataETLAdapter extends NkCustomObject {
    void execute(ReduceConfig config);
}
