package cn.nkpro.ts5.engine.elasticearch.model;

import cn.nkpro.ts5.engine.elasticearch.ESAnalyzerType;
import cn.nkpro.ts5.engine.elasticearch.ESFieldType;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESDynamicTemplate;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESField;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bean on 2020/7/6.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ESDocument("document")
// 不需要分词的文本：状态、枚举、常量等，不能模糊查询，只能全文匹配
@ESDynamicTemplate(value = "keyword",   match = "*_keyword",mappingType = ESFieldType.Keyword)
// 分词文本：需要进行分词的 中文或中英文混合 的文本
@ESDynamicTemplate(value = "text",      match = "*_text",   mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ik_max_word,    original = true)
@ESDynamicTemplate(value = "text$",     match = "*_text$",  mappingType = ESFieldType.Text,     analyzer = ESAnalyzerType.ik_max_word,    original = true, copyToKeyword = true)
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
public class DocBaseES extends ESDoc {

    @ESField(type= ESFieldType.Keyword)
    private String classify;

    @ESField(type= ESFieldType.Keyword)
    private String docType;

    @ESField(type= ESFieldType.Keyword)
    private String docTypeDesc;

    @ESField(type= ESFieldType.Keyword)
    private String docNumber;

    @ESField(type= ESFieldType.Keyword)
    private String docState;

    @ESField(type= ESFieldType.Keyword)
    private String docStateDesc;

    @ESField(type= ESFieldType.Integer)
    private Integer docDefVersion;

    @ESField(type= ESFieldType.Date, format = "epoch_second||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd")
    private Long createdTime;

    @ESField(type= ESFieldType.Date, format = "epoch_second||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd")
    private Long updatedTime;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word, copyToKeyword = true, original = true)
    private String docName;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word, copyToKeyword = true)
    private String docDesc;

    @ESField(type= ESFieldType.Keyword)
    private String preDocId;

    @ESField(type= ESFieldType.Keyword)
    private String[] tags;

    @ESField(type= ESFieldType.Keyword)
    private String partnerId;

    @ESField(type= ESFieldType.Keyword)
    private String processInstanceId;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.standard, copyToKeyword = true, original = true)
    private String partnerName;
}
