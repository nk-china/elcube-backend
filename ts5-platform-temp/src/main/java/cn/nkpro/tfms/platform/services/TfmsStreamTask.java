package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.basis.TfmsCustomObject;

public interface TfmsStreamTask extends TfmsCustomObject {

    void onMessage(TfmsStreamTaskInfo info);
}
