package cn.nkpro.groovy.cards.finance

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractCard
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

// todo 待开发卡片
@Order(10003)
@NkNote("账单偿还")
@Component("NkCardRepayment")
class NkCardRepayment extends NkAbstractCard<Map,Map> {
}
