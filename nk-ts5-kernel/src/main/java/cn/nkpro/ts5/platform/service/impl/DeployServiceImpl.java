package cn.nkpro.ts5.platform.service.impl;

import cn.nkpro.ts5.basic.secret.DesCbcUtil;
import cn.nkpro.ts5.co.NkCustomObjectManager;
import cn.nkpro.ts5.co.NkCustomScriptObject;
import cn.nkpro.ts5.co.PlatformScriptV;
import cn.nkpro.ts5.co.service.NkScriptManager;
import cn.nkpro.ts5.docengine.model.DocDefHV;
import cn.nkpro.ts5.docengine.service.NkDocDefService;
import cn.nkpro.ts5.platform.gen.PlatformRegistry;
import cn.nkpro.ts5.platform.model.WebMenuBO;
import cn.nkpro.ts5.platform.service.DeployService;
import cn.nkpro.ts5.platform.service.PlatformRegistryService;
import cn.nkpro.ts5.platform.service.WebMenuService;
import cn.nkpro.ts5.security.UserAuthorizationService;
import cn.nkpro.ts5.security.bo.UserGroupBO;
import cn.nkpro.ts5.security.gen.AuthLimit;
import cn.nkpro.ts5.security.gen.AuthPermission;
import cn.nkpro.ts5.task.NkBpmDefService;
import cn.nkpro.ts5.task.model.ResourceDefinition;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DeployServiceImpl implements DeployService {

    private String secretKey = "AEF1D52777444FDDBC354928D7D2BFD3";
    private String iv = "20201218";

    @Autowired@SuppressWarnings("all")
    private PlatformRegistryService registryService;
    @Autowired@SuppressWarnings("all")
    private WebMenuService menuService;
    @Autowired@SuppressWarnings("all")
    private UserAuthorizationService authorizationService;
    @Autowired@SuppressWarnings("all")
    private NkCustomObjectManager customObjectManager;
    @Autowired@SuppressWarnings("all")
    private NkDocDefService docDefService;
    @Autowired@SuppressWarnings("all")
    private NkBpmDefService bpmDefService;
    @Autowired@SuppressWarnings("all")
    private NkScriptManager scriptManager;

    @Override
    public String export(JSONObject config){

        JSONObject export = new JSONObject();

        if(config.getBooleanValue("includeRegistry")){
            export.put("registries",registryService.getAllByType(null));
        }
        if(config.getBooleanValue("includeMenu")){
            export.put("menus",menuService.getMenus(false));
        }
        if(config.getBooleanValue("includeAuth")){
            export.put("groups",authorizationService.getGroupBOs()
                    .stream()
                    .filter(group->!group.getGroupId().startsWith("nk-default-"))
                    .collect(Collectors.toList()));
            export.put("perms",authorizationService.getPerms());
            export.put("limits",authorizationService.getLimits(null)
                    .stream().map(limit->authorizationService.getLimitDetail(limit.getLimitId()))
                    .collect(Collectors.toList()));
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
                    ).collect(Collectors.toList())
            );
        }

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


        if(data.containsKey("registries")){
            registryService.doUpdate(data.getJSONArray("registries").toJavaList(PlatformRegistry.class));
        }
        if(data.containsKey("menus")){
            menuService.doUpdate(data.getJSONArray("menus").toJavaList(WebMenuBO.class));
        }
        if(data.containsKey("limits")){
            data.getJSONArray("limits").toJavaList(AuthLimit.class)
                    .forEach(limit -> authorizationService.updateLimit(limit));
        }
        if(data.containsKey("perms")){
            data.getJSONArray("perms").toJavaList(AuthPermission.class)
                    .forEach(perm -> authorizationService.updatePerm(perm));
        }
        if(data.containsKey("groups")){
            data.getJSONArray("groups").toJavaList(UserGroupBO.class)
                    .stream()
                    .filter(group->!group.getGroupId().startsWith("nk-default-"))
                    .forEach(group -> authorizationService.updateGroup(group));
        }
        if(data.containsKey("scripts")){
            data.getJSONArray("scripts").toJavaList(PlatformScriptV.class)
                    .forEach(scriptV -> scriptManager.doActive(scriptV, true));
        }
        if(data.containsKey("bpmDefs")){
            data.getJSONArray("bpmDefs").toJavaList(ResourceDefinition.class)
                    .forEach(definition -> bpmDefService.deploy(definition));
        }
        if(data.containsKey("docTypes")){
            data.getJSONArray("docTypes").toJavaList(DocDefHV.class)
                    .forEach(docDefHV -> docDefService.doActive(docDefHV));
        }

        return exceptions;
    }
}
