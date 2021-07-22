package cn.nkpro.ts5.services;

import com.alibaba.fastjson.JSONObject;

public interface TfmsDefDeployAble {
    int deployOrder();
    Object deployExport(JSONObject config);
    void deployImport(Object data);
}
