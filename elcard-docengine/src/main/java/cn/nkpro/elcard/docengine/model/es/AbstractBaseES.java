/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine.model.es;

import cn.nkpro.elcard.data.elasticearch.AbstractESModel;
import cn.nkpro.elcard.data.elasticearch.ESAnalyzerType;
import cn.nkpro.elcard.data.elasticearch.ESFieldType;
import cn.nkpro.elcard.data.elasticearch.annotation.ESField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by bean on 2020/7/6.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractBaseES extends AbstractESModel {

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
