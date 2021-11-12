package cn.nkpro.ts5.platform.service.impl;

import cn.nkpro.ts5.basic.secret.DesCbcUtil;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.co.NkCustomScriptObject;
import cn.nkpro.ts5.docengine.service.NkDocDefService;
import cn.nkpro.ts5.platform.service.DeployService;
import cn.nkpro.ts5.platform.service.PlatformRegistryService;
import cn.nkpro.ts5.platform.service.WebMenuService;
import cn.nkpro.ts5.security.UserAuthorizationService;
import cn.nkpro.ts5.task.NkBpmDefService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeployServiceImpl implements DeployService {

    private String secretKey = "AEF1D52777444FDDBC354928D7D2BFD3";
    private String iv = "20201218";

    @Autowired
    private PlatformRegistryService registryService;
    @Autowired
    private WebMenuService menuService;
    @Autowired
    private UserAuthorizationService authorizationService;
    @Autowired
    private NkCustomObjectManager customObjectManager;
    @Autowired
    private NkDocDefService docDefService;
    @Autowired
    private NkBpmDefService bpmDefService;

    @Override
    public String export(JSONObject config){

        JSONObject export = new JSONObject();

        if(config.getBooleanValue("includeRegistry")){
            export.put("registries",registryService.getAll());
        }
        if(config.getBooleanValue("includeMenu")){
            export.put("menus",menuService.getMenus(false));
        }
        if(config.getBooleanValue("includeAuth")){
            export.put("groups",authorizationService.getGroupBOs());
            export.put("perms",authorizationService.getPerms());
            export.put("limits",authorizationService.getLimits(null));
        }
        if(config.getJSONArray("scripts")!=null){
            export.put("scripts",
                    config.getJSONArray("scripts").stream().map(beanName->
                            customObjectManager.getCustomObject((String) beanName, NkCustomScriptObject.class).getScriptDef()
                    ).collect(Collectors.toList())
            );
        }
        if(config.getJSONArray("docTypes")!=null){
            export.put("docTypes",
                    config.getJSONArray("docTypes").stream().map(docType->
                            docDefService.getDocDefLatestActive((String) docType)
                    ).filter(Objects::nonNull).collect(Collectors.toList())
            );
        }
        if(config.getJSONArray("bpmDefs")!=null){
            export.put("bpmDefs",
                    config.getJSONArray("bpmDefs").stream().map(definitionId->
                            bpmDefService.getProcessDefinition((String) definitionId)
                                .getXml()
                    ).collect(Collectors.toList())
            );
        }

        if(config.getBooleanValue("compress")){
            return DesCbcUtil.encode(export.toJSONString(),secretKey,iv);
        }

        return export.toString();
    }

    @Override
    public void imports(String pointsTxt) {
        String uncompress = pointsTxt.startsWith("{")?pointsTxt:DesCbcUtil.decode(pointsTxt,secretKey,iv);
        HashMap data = JSON.parseObject(uncompress,HashMap.class);

        System.out.println(data);
    }
}
