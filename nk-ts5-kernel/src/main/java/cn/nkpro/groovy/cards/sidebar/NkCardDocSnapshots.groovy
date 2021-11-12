package cn.nkpro.groovy.cards.sidebar

import cn.nkpro.ts5.docengine.NkAbstractCard
import cn.nkpro.ts5.docengine.gen.DocRecord
import cn.nkpro.ts5.docengine.model.DocDefIV
import cn.nkpro.ts5.docengine.model.DocHV
import cn.nkpro.ts5.docengine.service.NkDocHistoryService
import cn.nkpro.ts5.annotation.NkNote
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@NkNote("变更历史")
@Component("NkCardDocSnapshots")
class NkCardDocSnapshots extends NkAbstractCard<List<DocRecord>,Map> {

    @Autowired@SuppressWarnings("all")
    private NkDocHistoryService docHistoryService

    @Override
    String getPosition() {
        return POSITION_SIDEBAR
    }

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
