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

import cn.nkpro.elcube.co.NkCustomObject;
import cn.nkpro.elcube.docengine.model.DocDefHV;
import cn.nkpro.elcube.docengine.model.DocHBasis;
import cn.nkpro.elcube.docengine.model.DocHPersistent;
import cn.nkpro.elcube.docengine.model.DocHV;

public interface NkDocProcessor extends NkCustomObject {


    EnumDocClassify classify();

    DocHV detail(DocDefHV def, DocHBasis docHD);

    DocHV toCreate(DocDefHV def, DocHV preDoc);

    DocHV doUpdate(DocHV doc, DocHV original, String optSource);

    DocHV calculate(DocHV doc, String fromCard, Object options);

    Object call(DocHV doc, String fromCard, String method, Object options);

    void doOnBpmKilled(DocHV docHV, String processKey, String optSource);

    DocHV random(DocHV doc);

    DocHBasis deserialize(DocDefHV def, DocHPersistent docHD);
}
