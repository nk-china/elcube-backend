package cn.nkpro.tfms.platform.services;

import com.alibaba.fastjson.JSONObject;

public interface TfmsDefDeployAble {
    int deployOrder();
    Object deployExport(JSONObject config);
    void deployImport(Object data);
}
