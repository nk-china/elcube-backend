package cn.nkpro.ts5.engine.elasticearch.model;


import cn.nkpro.ts5.engine.elasticearch.ESAnalyzerType;
import cn.nkpro.ts5.engine.elasticearch.ESFieldType;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESDynamicTemplate;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESDynamicTemplates;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESField;
import cn.nkpro.ts5.exception.TfmsDefineException;
import cn.nkpro.ts5.exception.TfmsSystemException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by bean on 2020/6/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractESDoc {

    @SuppressWarnings("unused")
    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word, searchAnalyzer = ESAnalyzerType.ik_smart)
    private List<String> $keyword = new ArrayList<>();

    @Getter
    private Map<String,Object> dynamics = new HashMap<>();
    /**
     * @see ESDynamicTemplate
     * @param key 添加的动态字段的名称，规则参考ESRoot
     * @param value 值
     */
    public void addDynamicField(String key, Object value){
        dynamics.put(key, value);
    }

    public void validateDynamic(){
        ESDynamicTemplate[] dynamicTemplates = dynamicTemplates();
        dynamics.keySet()
                .forEach(key->
                        Assert.isTrue(
                                Arrays.stream(dynamicTemplates)
                                        .anyMatch(template->
                                                key.contains(template.match().replaceAll("[*]",""))
                                        ),
                                String.format("找不到可以匹配字段%s的动态模版", key)
                        )
                );
    }

    public Map<String,Object> toSource(){

        return (JSONObject) JSON.toJSON(this);
    }

    private ESDynamicTemplate[] dynamicTemplates(){
        ESDynamicTemplate[] array;
        ESDynamicTemplates dynamicTemplates = getClass().getAnnotation(ESDynamicTemplates.class);
        ESDynamicTemplate dynamicTemplate = getClass().getAnnotation(ESDynamicTemplate.class);
        if(dynamicTemplates!=null){
            array = dynamicTemplates.value();
        }else if(dynamicTemplate!=null){
            array = new ESDynamicTemplate[]{dynamicTemplate};
        }else{
            array = new ESDynamicTemplate[0];
        }
        return array;
    }
}
