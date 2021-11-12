package cn.nkpro.ts5.platform;

import com.alibaba.fastjson.JSONObject;

public interface DeployAble {
    int deployOrder();
    Object deployExport(JSONObject config);
    void deployImport(Object data);
}
