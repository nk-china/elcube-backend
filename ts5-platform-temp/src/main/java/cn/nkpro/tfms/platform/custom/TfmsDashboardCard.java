package cn.nkpro.tfms.platform.custom;

import cn.nkpro.tfms.platform.basis.TfmsCustomObject;

public abstract class TfmsDashboardCard<DT> implements TfmsCustomObject {

    public abstract String getName();

    public abstract DT getData();

    public int getW(){
        return 4;
    }

    public int getH(){
        return 6;
    }
}
