package cn.nkpro.easis.docengine.model.es;

import cn.nkpro.easis.data.elasticearch.ESFieldType;
import cn.nkpro.easis.data.elasticearch.annotation.ESDocument;
import cn.nkpro.easis.data.elasticearch.annotation.ESField;
import cn.nkpro.easis.data.elasticearch.annotation.ESId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bean on 2020/7/6.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ESDocument("doc-ext")
public class DocExtES extends AbstractBaseES {

    @ESId
    @ESField(type= ESFieldType.Keyword)
    private String customId;

    @ESField(type= ESFieldType.Keyword)
    private String customType;

    @ESField(type= ESFieldType.Keyword)
    private String docId;
}