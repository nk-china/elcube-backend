package cn.nkpro.tfms.platform.services;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface TfmsDefDeployService {
//    String buildExportKey(JSONObject config);

    String defExport(JSONObject config);

    void defImport(String data);
}
