package cn.nkpro.ts5.data.elasticearch;


import cn.nkpro.ts5.data.elasticearch.annotation.ESDynamicTemplate;
import cn.nkpro.ts5.data.elasticearch.annotation.ESDynamicTemplates;
import cn.nkpro.ts5.data.elasticearch.annotation.ESField;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by bean on 2020/6/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
// 不需要分词的文本：状态、枚举、常量等，不能模糊查询，只能全文匹配
@ESDynamicTemplate(value = "keyword",   match = "*_keyword",mappingType = ESFieldType.Keyword)
// 分词文本：需要进行分词的 中文或中英文混合 的文本
@ESDynamicTemplate(value = "text",      match = "*_text",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ik_max_word,    original = true)
@ESDynamicTemplate(value = "text$",     match = "*_text$",  mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ik_max_word,    original = true, copyToKeyword = true)

@ESDynamicTemplate(value = "completion",match = "*_completion",   mappingType = ESFieldType.Completion,analyzer = ESAnalyzerType.ik_max_word,    original = true)
// 名字：人名、企业名称等
@ESDynamicTemplate(value = "name",      match = "*_name",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.standard,       original = true)
@ESDynamicTemplate(value = "name$",     match = "*_name$",  mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.standard,       original = true, copyToKeyword = true)
// 序列号：如身份证号码、单据号码等字符串
@ESDynamicTemplate(value = "serial",    match = "*_serial", mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, original = true)
@ESDynamicTemplate(value = "serial$",   match = "*_serial$",mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, original = true, copyToKeyword = true)
// 邮箱地址
@ESDynamicTemplate(value = "mail",      match = "*_mail",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, original = true)
@ESDynamicTemplate(value = "mail$",     match = "*_mail$",  mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, original = true, copyToKeyword = true)
// 网络地址
@ESDynamicTemplate(value = "url",       match = "*_url",    mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, original = true)
@ESDynamicTemplate(value = "url$",      match = "*_url$",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, original = true, copyToKeyword = true)
// 手机号
@ESDynamicTemplate(value = "phone",     match = "*_phone",  mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, original = true)
@ESDynamicTemplate(value = "phone$",    match = "*_phone$", mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, original = true, copyToKeyword = true)

// todo 拼音搜索待实现
//@ESDynamicTemplate(value = "text",      match = "*_pinyin",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ik_max_word)

@ESDynamicTemplate(value = "bool",      match = "*_bool*",  mappingType = ESFieldType.Boolean)
@ESDynamicTemplate(value = "int",       match = "*_int*",   mappingType = ESFieldType.Integer)
@ESDynamicTemplate(value = "long",      match = "*_long",   mappingType = ESFieldType.Long)
@ESDynamicTemplate(value = "float",     match = "*_float",  mappingType = ESFieldType.Float)
@ESDynamicTemplate(value = "double",    match = "*_double", mappingType = ESFieldType.Double)
@ESDynamicTemplate(value = "date",      match = "*_date",   mappingType = ESFieldType.Date, format = "epoch_second||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd")
@ESDynamicTemplate(value = "obj",       match = "*_obj*",   mappingType = ESFieldType.Object)
public abstract class AbstractESModel {

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
        if(value instanceof Date){
            dynamics.put(key,((Date) value).getTime()/1000);
        }else{
            dynamics.put(key, value);
        }
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
