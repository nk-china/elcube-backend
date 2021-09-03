package cn.nkpro.ts5.docengine.model.es;

import cn.nkpro.ts5.data.elasticearch.ESFieldType;
import cn.nkpro.ts5.data.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.data.elasticearch.annotation.ESField;
import cn.nkpro.ts5.data.elasticearch.annotation.ESId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bean on 2020/7/6.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ESDocument("document-custom")
public class CustomES extends AbstractBaseES {

    @ESId
    @ESField(type= ESFieldType.Keyword)
    private String customId;

    @ESField(type= ESFieldType.Keyword)
    private String customType;

    @ESField(type= ESFieldType.Keyword)
    private String docId;
}