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
package cn.nkpro.elcard.docengine.service;

import cn.nkpro.elcard.data.elasticearch.LimitQueryBuilder;
import cn.nkpro.elcard.docengine.model.DocHV;
import org.elasticsearch.index.query.QueryBuilder;

public interface NkDocPermService {



    //String MODE_ADD =     "NEW";
    String MODE_READ =    "READ";
    String MODE_WRITE =   "WRITE";
    //String MODE_REMOVE =  "REMOVE";


    void filterDocCards(String mode, DocHV docHV);

    void assertHasDocPerm(String mode, String docType);

    boolean hasDocPerm(String mode, String docType);

    void assertHasDocPerm(String mode, String docId, String docType);

    boolean hasDocPerm(String mode, String docId, String docType);

    LimitQueryBuilder buildIndexFilter(String index);

    QueryBuilder buildDocFilter(String mode, String docType, String typeKey, boolean ignoreLimit);
}
