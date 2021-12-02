/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
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
