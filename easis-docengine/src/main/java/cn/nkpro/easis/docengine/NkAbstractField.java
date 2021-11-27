package cn.nkpro.easis.docengine;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.co.NkAbstractCustomScriptObject;
import cn.nkpro.easis.co.NkScriptV;
import cn.nkpro.easis.docengine.cards.NkBaseContext;
import cn.nkpro.easis.docengine.cards.NkCalculateContext;
import cn.nkpro.easis.docengine.cards.NkDynamicFormDefI;
import cn.nkpro.easis.co.easy.EasySingle;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.EvaluationContext;

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

    @Override
    public void beforeCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext) {

    }

    @Override
    public void processOptions(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkBaseContext baseContext) {

    }

    @Override
    public void afterCalculate(NkDynamicFormDefI field, EvaluationContext context, EasySingle card, NkCalculateContext calculateContext) {

    }
}
