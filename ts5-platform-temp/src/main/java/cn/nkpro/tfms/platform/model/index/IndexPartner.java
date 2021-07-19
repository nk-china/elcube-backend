package cn.nkpro.tfms.platform.model.index;

import cn.nkpro.tfms.platform.elasticearch.annotation.*;
import cn.nkpro.tfms.platform.elasticearch.ESRoot;
import lombok.Data;

/**
 * Created by bean on 2020/6/19.
 */
@Data
@ESDocument("partner")
public class IndexPartner extends ESRoot {

    @ESId
    @ESField(type= ESFieldType.Keyword)
    private String partnerId;

    @ESField(type= ESFieldType.Keyword)
    private String partnerType;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word, original = true, copyToKeyword = true)
    private String partnerName;

    @ESField(type= ESFieldType.Keyword)
    private String[] roles;

    @ESField(type= ESFieldType.Keyword)
    private String[] roleIds;

    @ESField(type= ESFieldType.Keyword)
    private String[] tags;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word)
    private String remark;

    @ESField(type= ESFieldType.Long)
    private Long createdTime;

    @ESField(type= ESFieldType.Long)
    private Long updatedTime;
}
