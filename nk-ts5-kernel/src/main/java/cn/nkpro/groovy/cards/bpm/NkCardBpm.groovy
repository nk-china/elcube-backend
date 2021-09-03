package cn.nkpro.groovy.cards.bpm

import cn.nkpro.ts5.docengine.abstracts.NkAbstractCard
import cn.nkpro.ts5.wsdoc.annotation.WsDocNote
import org.springframework.stereotype.Component

@WsDocNote("工作流")
@Component("NkCardBpm")
class NkCardBpm extends NkAbstractCard<BpmData,BpmDef> {

}
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
