package cn.nkpro.tfms.platform.model.index;

import cn.nkpro.tfms.platform.elasticearch.annotation.*;
import lombok.Getter;
import lombok.Setter;

@ESDocument("document-item")
// 不需要分词的文本：状态、枚举、常量等，不能模糊查询，只能全文匹配
@ESDynamicTemplate(value = "keyword",   match = "*_keyword",mappingType = ESFieldType.Keyword)
// 分词文本：需要进行分词的 中文或中英文混合 的文本
@ESDynamicTemplate(value = "text",      match = "*_text",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ik_max_word)
@ESDynamicTemplate(value = "text$",     match = "*_text$",  mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ik_max_word, copyToKeyword = true)
// 名字：人名、企业名称等
@ESDynamicTemplate(value = "name",      match = "*_name",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.standard)
@ESDynamicTemplate(value = "name$",     match = "*_name$",  mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.standard, copyToKeyword = true)
// 序列号：如身份证号码、单据号码等字符串
@ESDynamicTemplate(value = "serial",    match = "*_serial", mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer)
@ESDynamicTemplate(value = "serial$",   match = "*_serial$",mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, copyToKeyword = true)
// 邮箱地址
@ESDynamicTemplate(value = "mail",      match = "*_mail",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer)
@ESDynamicTemplate(value = "mail$",     match = "*_mail$",  mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, copyToKeyword = true)
// 网络地址
@ESDynamicTemplate(value = "url",       match = "*_url",    mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer)
@ESDynamicTemplate(value = "url$",      match = "*_url$",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, copyToKeyword = true)
// 手机号
@ESDynamicTemplate(value = "phone",     match = "*_phone",  mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer)
@ESDynamicTemplate(value = "phone$",    match = "*_phone$", mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ngram_analyzer, copyToKeyword = true)

// todo 拼音搜索待实现
//@ESDynamicTemplate(value = "text",      match = "*_pinyin",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ik_max_word)

@ESDynamicTemplate(value = "bool",      match = "*_bool*",  mappingType = ESFieldType.Boolean)
@ESDynamicTemplate(value = "int",       match = "*_int*",   mappingType = ESFieldType.Integer)
@ESDynamicTemplate(value = "long",      match = "*_long",   mappingType = ESFieldType.Long)
@ESDynamicTemplate(value = "float",     match = "*_float",  mappingType = ESFieldType.Float)
@ESDynamicTemplate(value = "double",    match = "*_double", mappingType = ESFieldType.Double)
@ESDynamicTemplate(value = "data",      match = "*_date",   mappingType = ESFieldType.Date)
@ESDynamicTemplate(value = "obj",       match = "*_obj*",   mappingType = ESFieldType.Object)
public class IndexDocItem extends IndexDocFields{

    @ESId
    @ESField(type= ESFieldType.Keyword)
    @Getter@Setter
    private String itemId;
    /**
     * \@FLOW componentName 等
     */
    @ESField(type= ESFieldType.Keyword)
    @Getter@Setter
    private String itemType;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word, copyToKeyword = true, original = true)
    @Getter@Setter
    private String itemName;

    @ESField(type= ESFieldType.Keyword)
    @Getter@Setter
    private String itemState;

    @ESField(type= ESFieldType.Keyword)
    @Getter@Setter
    private String itemStateDesc;

    @ESField(type= ESFieldType.Keyword)
    @Getter@Setter
    private String docId;
}
