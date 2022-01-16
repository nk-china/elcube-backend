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
import cn.nkpro.elcube.docengine.service.impl.NkDocFinder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface NkDocEngine {

    /**
     * 根据单据ID获取一个单据
     * @param docId docId
     * @return DocHV
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    DocHV detail(String docId);

    /**
     * 根据单据类型和单据业务主键获取一个单据
     * @param docType docType
     * @param businessKey businessKey
     * @see #detail(String)
     * @return DocHV
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    DocHV detail(String docType, String businessKey);

    /**
     * 构建一个查询器，通过索引字段检索单据
     * @param docType 查询的单据类型
     * @return NkDocFinder
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    NkDocFinder find(String... docType);

    @Transactional(propagation = Propagation.REQUIRED)
    DocHV create(String docType, String preDocId, String optSource, Function function);

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    DocHV calculate(DocHV doc, String fromCard, Object options);

    @Transactional
    DocHV doUpdate(String docId, String optSource, Function function);

    @Transactional
    DocHV doUpdate(String docType, String businessKey, String optSource, Function function);

    @Transactional
    DocHV doUpdateAgain(String docId, String optSource, Function function);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    DocHV random(DocHV doc);

    @FunctionalInterface
    interface Function{
        void apply(DocHV doc);
    }
}
