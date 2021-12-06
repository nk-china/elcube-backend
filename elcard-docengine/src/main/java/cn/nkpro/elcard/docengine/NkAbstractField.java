/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine;

import cn.nkpro.elcard.annotation.NkNote;
import cn.nkpro.elcard.co.NkAbstractCustomScriptObject;
import cn.nkpro.elcard.co.NkScriptV;
import cn.nkpro.elcard.docengine.cards.NkBaseContext;
import cn.nkpro.elcard.docengine.cards.NkCalculateContext;
import cn.nkpro.elcard.docengine.cards.NkDynamicFormDefI;
import cn.nkpro.elcard.co.easy.EasySingle;
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
