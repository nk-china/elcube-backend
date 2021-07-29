package cn.nkpro.ts5.engine.devops;

import com.alibaba.fastjson.JSONObject;

public interface TfmsDefDeployAble {
    int deployOrder();
    Object deployExport(JSONObject config);
    void deployImport(Object data);
}
