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
package cn.nkpro.easis.docengine.interceptor;

import cn.nkpro.easis.co.NkCustomObject;
import cn.nkpro.easis.docengine.model.DocHV;
import cn.nkpro.easis.docengine.service.impl.NkDocCycleContext;

public interface NkDocCycleInterceptor extends NkCustomObject {

//    default DocHV beforeCreate(DocHV doc, DocHV ref){return doc;}
//    default DocHV afterCreated(DocHV doc, DocHV ref){return doc;}
//    default DocHV afterCopied(DocHV doc, DocHV ref){return doc;}
//
//    default DocHV beforeCalculate(DocHV doc){return doc;}
//    default DocHV afterCalculated(DocHV doc){return doc;}
//
//    default DocHV beforeUpdate(DocHV doc, DocHV original){return doc;}
//    default DocHV afterUpdated(DocHV doc, DocHV original){return doc;}
//    @Transactional(propagation = Propagation.NEVER)
//    default void afterUpdateCommit(DocHV doc){}
//
//    default DocHV beforeDelete(DocHV doc, DocHV original){return doc;}
//    default DocHV afterDeleted(DocHV doc, DocHV original){return doc;}
//    @Transactional(propagation = Propagation.NEVER)
//    default void afterDeleteCommit(DocHV doc){}

    default void apply(DocHV doc, NkDocCycleContext context){}
}
