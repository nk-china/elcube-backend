package cn.nkpro.ts5.docengine;

import cn.nkpro.ts5.annotation.NkNote;
import cn.nkpro.ts5.co.NkAbstractCustomScriptObject;
import cn.nkpro.ts5.co.NkScriptV;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class NkAbstractField extends NkAbstractCustomScriptObject implements NkField {

    @Getter
    private String name;

    public NkAbstractField(){
        super();
        this.name = Optional.ofNullable(getClass().getAnnotation(NkNote.class))
                .map(NkNote::value)
                .orElse(beanName);
    }
    @Override
    public Map<String,String> getVueTemplate(){

        NkScriptV scriptDef = scriptDefHV();

        if(scriptDef!=null){
            Map<String,String> vueMap = new HashMap<>();
            if(StringUtils.isNotBlank(scriptDef.getVueMain())){
                vueMap.put(scriptDef.getScriptName(),scriptDef.getVueMain());
            }
            if(StringUtils.isNotBlank(scriptDef.getVueDefs())){
                JSONArray array = JSON.parseArray(scriptDef.getVueDefs());
                array.forEach((item)->{
                    int index = array.indexOf(item);
                    vueMap.put(scriptDef.getScriptName()+"Def"+(index==0?"":index), (String) item);
                });
            }
            return vueMap;
        }
        return Collections.emptyMap();
    }
}
