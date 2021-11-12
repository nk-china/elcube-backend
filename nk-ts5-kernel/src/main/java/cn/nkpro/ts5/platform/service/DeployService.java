package cn.nkpro.ts5.platform.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface DeployService {
    String export(JSONObject config);

    List<String> imports(String pointsTxt);
}
