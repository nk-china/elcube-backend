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

import cn.nkpro.elcard.data.elasticearch.ESFieldType;
import cn.nkpro.elcard.data.elasticearch.annotation.ESDocument;
import cn.nkpro.elcard.data.elasticearch.annotation.ESField;
import cn.nkpro.elcard.data.elasticearch.annotation.ESId;
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