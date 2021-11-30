package cn.nkpro.easis.components.defaults.cards.bpm

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import org.springframework.stereotype.Component

/*
 * 暂时没用到，待作废
 */
@Deprecated
@SuppressWarnings("unused")
@NkNote("工作流")
@Component("NkCardBpm")
class NkCardBpm extends NkAbstractCard<BpmData,BpmDef> {

    @SuppressWarnings("unused")
    class BpmData{
        private String processKey

        String getProcessKey() {
            return processKey
        }

        void setProcessKey(String processKey) {
            this.processKey = processKey
        }
    }

    @SuppressWarnings("unused")
    class BpmDef{

        private String processKey
        private String startBy
        private String rollbackTo

        String getProcessKey() {
            return processKey
        }

        void setProcessKey(String processKey) {
            this.processKey = processKey
        }

        String getStartBy() {
            return startBy
        }

        void setStartBy(String startBy) {
            this.startBy = startBy
        }

        String getRollbackTo() {
            return rollbackTo
        }

        void setRollbackTo(String rollbackTo) {
            this.rollbackTo = rollbackTo
        }
    }
}

