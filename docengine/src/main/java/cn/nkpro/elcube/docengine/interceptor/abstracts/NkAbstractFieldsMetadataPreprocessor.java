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
package cn.nkpro.elcube.docengine.interceptor.abstracts;

import cn.nkpro.elcube.co.NkAbstractCustomScriptObject;
import cn.nkpro.elcube.docengine.interceptor.NkFieldsMetadataPreprocessor;
import cn.nkpro.elcube.docengine.model.DocHV;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;

public abstract class NkAbstractFieldsMetadataPreprocessor
        extends NkAbstractCustomScriptObject
        implements NkFieldsMetadataPreprocessor {
    @Override
    public void processMeta(IXDocReport report) {

    }

    @Override
    public void processData(DocHV doc, IContext context) {

    }
}