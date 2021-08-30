package cn.nkpro.ts5.engine.elasticearch.model;

import cn.nkpro.ts5.engine.elasticearch.ESFieldType;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESField;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESId;
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
    private String itemId;

    @ESField(type= ESFieldType.Keyword)
    private String itemType;

    @ESField(type= ESFieldType.Keyword)
    private String docId;
}