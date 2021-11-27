package cn.nkpro.easis.platform.service.impl;

import cn.nkpro.easis.basic.secret.DesCbcUtil;
import cn.nkpro.easis.platform.DeployAble;
import cn.nkpro.easis.platform.service.DeployService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeployServiceImpl implements DeployService {

    private String secretKey = "AEF1D52777444FDDBC354928D7D2BFD3";
    private String iv = "20201218";

    @Autowired@SuppressWarnings("all")
    private List<DeployAble> deployAbles;

    @Override
    public JSONArray load(){
        JSONArray exports = new JSONArray();
        deployAbles.forEach(deployAble -> deployAble.loadExport(exports));
        return exports;
    }

    @Override
    public String export(JSONObject config){

        JSONObject export = new JSONObject();

        deployAbles.forEach(deployAble -> deployAble.exportConfig(config, export));

        if(config.getBooleanValue("compress")){
            return DesCbcUtil.encode(export.toJSONString(),secretKey,iv);
        }

        return export.toString();
    }

    @Override
    @Transactional(noRollbackFor = {Exception.class})
    public List<String> imports(String pointsTxt) {

        List<String> exceptions = new ArrayList<>();

        String uncompress = pointsTxt.startsWith("{")?pointsTxt:DesCbcUtil.decode(pointsTxt,secretKey,iv);
        JSONObject data = JSON.parseObject(uncompress);

        deployAbles.forEach(deployAble -> deployAble.importConfig(data));

        return exceptions;
    }
}
