package cn.nkpro.ts5.engine.elasticearch.model;

import cn.nkpro.ts5.engine.elasticearch.ESFieldType;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESField;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bean on 2020/7/6.
 */
@EqualsAndHashCode
@Data
public class DocHES extends DocBaseES {

    @ESId
    @ESField(type= ESFieldType.Keyword)
    private String docId;
}
