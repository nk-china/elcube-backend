package cn.nkpro.tfms.platform.model.index;

import cn.nkpro.tfms.platform.elasticearch.ESRoot;
import cn.nkpro.tfms.platform.elasticearch.annotation.*;
import lombok.Data;

/**
 * Created by bean on 2020/7/6.
 */
@Data
public class IndexDocFields extends ESRoot {

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

    @ESField(type= ESFieldType.Keyword)
    private String projectState;

    @ESField(type= ESFieldType.Keyword)
    private String projectStateDesc;

    @ESField(type= ESFieldType.Integer)
    private Integer docDefVersion;

    @ESField(type= ESFieldType.Long)
    private Long createdTime;

    @ESField(type= ESFieldType.Long)
    private Long updatedTime;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word, copyToKeyword = true, original = true)
    private String docName;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word, copyToKeyword = true)
    private String docDesc;

    @ESField(type= ESFieldType.Keyword)
    private String refObjectId;

    @ESField(type= ESFieldType.Keyword)
    private String preDocId;

    @ESField(type= ESFieldType.Keyword)
    private String[] tags;

    @ESField(type= ESFieldType.Keyword)
    private String partnerId;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.standard, copyToKeyword = true, original = true)
    private String partnerName;
}
