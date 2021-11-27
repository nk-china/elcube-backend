package cn.nkpro.easis.docengine.model.es;

import cn.nkpro.easis.data.elasticearch.ESAnalyzerType;
import cn.nkpro.easis.data.elasticearch.ESFieldType;
import cn.nkpro.easis.data.elasticearch.annotation.ESDocument;
import cn.nkpro.easis.data.elasticearch.annotation.ESField;
import cn.nkpro.easis.data.elasticearch.annotation.ESId;
import cn.nkpro.easis.docengine.model.DocHV;
import cn.nkpro.easis.utils.BeanUtilz;
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

    @ESField(type= ESFieldType.Keyword)
    private String refObjectId;

    @ESField(type= ESFieldType.Text,analyzer = ESAnalyzerType.ik_max_word)
    private String docDesc;

    public static DocHES from(DocHV docHV){
        DocHES doc = BeanUtilz.copyFromObject(docHV, DocHES.class);
        docHV.getDynamics().forEach(doc::addDynamicField);
        return doc;
    }
}
