package cn.nkpro.tfms.platform.elasticearch;


import cn.nkpro.tfms.platform.elasticearch.annotation.ESAnalyzerType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import cn.nkpro.tfms.platform.elasticearch.annotation.ESField;
import cn.nkpro.tfms.platform.elasticearch.annotation.ESFieldType;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bean on 2020/6/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ESRoot {

    @SuppressWarnings("unused")
    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word, searchAnalyzer = ESAnalyzerType.ik_smart)
    private List<String> $keyword = new ArrayList<>();

    @Getter@Setter
    private Map<String,Object> dynamics;

    /**
     * @see cn.nkpro.tfms.platform.elasticearch.annotation.ESDynamicTemplate
     * @param key 添加的动态字段的名称，规则参考ESRoot
     * @param value 值
     */
    public void addDynamicField(String key, Object value){
        Assert.isTrue(key.contains("_"),String.format("dynamic key [ %s ] 不合法，必须以下划线[_]分割，以数据类型结尾", key));
        if(dynamics==null){
            dynamics = new HashMap<>();
        }
        dynamics.put(key, value);
    }
}
