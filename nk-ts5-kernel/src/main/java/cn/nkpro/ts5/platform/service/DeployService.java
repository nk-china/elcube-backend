package cn.nkpro.ts5.platform.service;

import com.alibaba.fastjson.JSONObject;

public interface DeployService {
    String export(JSONObject config);

    void imports(String pointsTxt);
}
