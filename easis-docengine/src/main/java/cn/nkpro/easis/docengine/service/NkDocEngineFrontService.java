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
package cn.nkpro.easis.docengine.service;

import cn.nkpro.easis.basic.PageList;
import cn.nkpro.easis.docengine.NkDocEngine;
import cn.nkpro.easis.docengine.gen.DocH;
import cn.nkpro.easis.docengine.model.DocHV;
import cn.nkpro.easis.docengine.model.DocState;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface NkDocEngineFrontService extends NkDocEngine {

    PageList<DocH> list(String docType, int offset, int rows, String orderBy);

    DocHV createForView(String docType, String preDocId);

    DocHV detailView(String docId, boolean edit);

    @Transactional(propagation = Propagation.NEVER)
    Object call(DocHV doc, String fromCard, String method, Object options);

    @Transactional
    DocHV doUpdateView(DocHV docHV, String optSource);

    void onBpmKilled(String docId, String processKey, String optSource);

    void reDataSync(DocHV doc);

    DocState state(String docId);
}
