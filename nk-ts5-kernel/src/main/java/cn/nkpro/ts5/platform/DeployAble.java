package cn.nkpro.ts5.platform;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface DeployAble {
    void loadExport(JSONArray exports);
    void exportConfig(JSONObject config,JSONObject export);
    void importConfig(JSONObject data);
}
