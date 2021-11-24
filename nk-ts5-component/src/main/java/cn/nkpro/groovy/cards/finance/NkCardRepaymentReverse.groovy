package cn.nkpro.groovy.cards.finance

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractCard
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

// todo 待开发卡片
@Order(10004)
@NkNote("账单红冲")
@Component("NkCardRepaymentReverse")
class NkCardRepaymentReverse extends NkAbstractCard<Map,Map> {
}
