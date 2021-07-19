package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.DefDocBpm;
import cn.nkpro.tfms.platform.model.po.DefDocIndexRule;
import cn.nkpro.tfms.platform.model.po.DefDocType;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2020/1/16.
 */

@Data
public class DefDocTypeBO extends DefDocType {

    public DefDocTypeBO(){
        this.customComponentsDef = new HashMap<>();
    }

    private List<DefDocStatusExt> status;

    private List<DefDocIndexRule> indexRules;

    private DefDocBpm bpm;

    private List<DefDocComponentBO> customComponents;

    private Map<String,Object> customComponentsDef;

    private String docHeaderComponent;

    private String[] docDefNames;

    private Map<String,Object> docDef;

    public String cacheKey(){
        return String.format("%s-%s",getDocType(),getVersion());
    }
}
