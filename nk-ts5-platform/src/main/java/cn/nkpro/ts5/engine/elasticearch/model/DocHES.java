package cn.nkpro.ts5.engine.elasticearch.model;

import cn.nkpro.ts5.engine.doc.model.DocHV;
import cn.nkpro.ts5.engine.elasticearch.ESAnalyzerType;
import cn.nkpro.ts5.engine.elasticearch.ESFieldType;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESDocument;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESField;
import cn.nkpro.ts5.engine.elasticearch.annotation.ESId;
import cn.nkpro.ts5.utils.BeanUtilz;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bean on 2020/7/6.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ESDocument("document")
public class DocHES extends AbstractBaseES {

    @ESId
    @ESField(type= ESFieldType.Keyword)
    private String docId;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word)
    private String docDesc;

    public static DocHES from(DocHV docHV){
        return BeanUtilz.copyFromObject(docHV, DocHES.class);
    }
}
