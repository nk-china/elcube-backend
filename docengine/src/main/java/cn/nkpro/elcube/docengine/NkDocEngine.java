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
package cn.nkpro.elcube.docengine;

import cn.nkpro.elcube.docengine.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface NkDocEngine {

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    DocHV detail(String docId);

    @Transactional(propagation = Propagation.REQUIRED)
    DocHV create(String docType, String preDocId, String optSource, Function function);

    @Transactional(propagation = Propagation.NEVER)
    DocHV calculate(DocHV doc, String fromCard, Object options);

    @Transactional
    DocHV doUpdate(String docId, String optSource, Function function);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    DocHV random(DocHV doc);

    @FunctionalInterface
    interface Function{
        void apply(DocHV doc);
    }
}
