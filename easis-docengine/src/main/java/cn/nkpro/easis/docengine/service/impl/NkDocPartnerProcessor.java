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
package cn.nkpro.easis.docengine.service.impl;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.docengine.EnumDocClassify;
import cn.nkpro.easis.docengine.model.DocDefHV;
import cn.nkpro.easis.docengine.model.DocHV;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@NkNote("伙伴")
@Component("NKDocPartnerProcessor")
public class NkDocPartnerProcessor extends NkDocTransactionProcessor {
    @Override
    public EnumDocClassify classify() {
        return EnumDocClassify.PARTNER;
    }

    @Override
    public String desc() {
        return "Partner | 伙伴";
    }

    @Override
    public DocHV toCreate(DocDefHV def, DocHV preDoc) {
        DocHV doc = super.toCreate(def, preDoc);
        doc.setDocName(null);
        return doc;
    }

    @Override
    public DocHV doUpdate(DocHV doc, DocHV original, String optSource) {
        doc.setPartnerName(doc.getDocName());
        doc =  super.doUpdate(doc, original, optSource);
        return doc;
    }
}
