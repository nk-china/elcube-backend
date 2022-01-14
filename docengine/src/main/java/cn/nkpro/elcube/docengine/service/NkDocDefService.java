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
import cn.nkpro.elcube.docengine.NkCard;
import cn.nkpro.elcube.docengine.gen.DocDefH;
import cn.nkpro.elcube.docengine.model.DocDefFlowV;
import cn.nkpro.elcube.docengine.model.DocDefHV;
import cn.nkpro.elcube.docengine.model.DocDefIV;

import java.util.List;
import java.util.Map;

public interface NkDocDefService {

    PageList<DocDefH> getPage(String docClassify,
                              String docType,
                              String state,
                              String keyword,
                              int from,
                              int rows,
                              String orderField,
                              String order);

    List<DocDefH> getAllDocTypes();

    List<DocDefH> getList(String docType, int page);

    Map<String, Object> options(String classify);

    DocDefIV getCardDescribe(String cardHandlerName);

    List<DocDefFlowV> getEntrance(String classify);

    DocDefHV doRun(DocDefHV docDefHV, boolean run);

    List<DocDefFlowV> getDocTypeFlows(String docType);

    DocDefHV getDocDefForRuntime(String docType);

    DocDefHV getDocDefForEdit(String docType, String version);

    DocDefHV doBreach(DocDefHV docDefHV);

    DocDefHV doActive(DocDefHV docDefHV);

    void doDelete(DocDefH docDefHV, boolean force);

    DocDefHV doUpdate(DocDefHV defDocTypeBO, boolean force);

    DocDefHV deserializeDef(DocDefHV docDefHV);

    Object callDef(Object def, String fromCard, Object options);

    void runLoopCards(String docId, DocDefHV docDefHV, boolean reCalc, boolean ignoreError, Function function);

    DocDefHV getDocDefLatestActive(String docType);

    @FunctionalInterface
    interface Function {
        void run(NkCard card, DocDefIV docDefIV) throws Exception;
    }
}