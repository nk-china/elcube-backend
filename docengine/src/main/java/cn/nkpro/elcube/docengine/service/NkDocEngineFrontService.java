/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.docengine.service;

import cn.nkpro.elcube.basic.PageList;
import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.model.DocHV;
import cn.nkpro.elcube.docengine.model.DocState;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface NkDocEngineFrontService extends NkDocEngine {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    PageList<DocH> list(String docType, int offset, int rows, String orderBy);

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    DocHV createForView(String docType, String preDocId);

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    DocHV detailView(String docId, boolean edit);

    @Transactional(propagation = Propagation.NEVER)
    Object call(DocHV doc, String fromCard, String method, Object options);

    @Transactional
    DocHV doUpdateView(DocHV docHV, String optSource);

    @Transactional
    void onBpmKilled(String docId, String processKey, String optSource);

    @Transactional
    void reDataSync(DocHV doc);

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    DocState state(String docId);
}
