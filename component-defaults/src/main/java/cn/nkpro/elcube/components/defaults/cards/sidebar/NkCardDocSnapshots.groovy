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
package cn.nkpro.elcube.components.defaults.cards.sidebar

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.docengine.NkAbstractCard
import cn.nkpro.elcube.docengine.gen.DocRecord
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
import cn.nkpro.elcube.docengine.service.NkDocHistoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("变更历史")
@Component("NkCardDocSnapshots")
class NkCardDocSnapshots extends NkAbstractCard<List<DocRecord>,Map> {

    @Autowired@SuppressWarnings("all")
    private NkDocHistoryService docHistoryService

    //@Override
    //String getPosition() {
    //    return POSITION_SIDEBAR
    //}

    @Override
    boolean enableDataDiff() {
        return false
    }
/**
 * 打开单据时，自定义获取数据
 * @param doc
 * @param data
 * @param map
 * @return
 */
    @Override
    List<DocRecord> afterGetData(DocHV doc, List<DocRecord> data, DocDefIV defIV, Map map) {
        return docHistoryService.getHistories(doc.getDocId(), 0)
    }

    /**
     * 历史记录的数据不保存，所以直接返回null
     * @param doc
     * @param data
     * @param map
     * @param original
     * @return
     */
    @Override
    List<DocRecord> beforeUpdate(DocHV doc, List<DocRecord> data, List<DocRecord> original, DocDefIV defIV, Map map) {
        return Collections.emptyList()
    }

    /**
     * 单据保存成功后，重新获取卡片数据
     * 这里有点特殊，因为历史交易是最后保存的，所以卡片这里读不到，必须在事务提交之后获取
     * @param doc
     * @param data
     * @param defIV
     * @param map
     */
    @Override
    void updateCommitted(DocHV doc, List<DocRecord> data, DocDefIV defIV, Map map) {
        doc.getData().put(defIV.getCardKey(), docHistoryService.getHistories(doc.getDocId(), 0))
    }

    @Override
    List<DocRecord> call(DocHV doc, List<DocRecord> data, DocDefIV defIV, Map map, Object options) {
        return docHistoryService.getHistories(doc.getDocId(), options as int)
    }
}
