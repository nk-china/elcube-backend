package cn.nkpro.groovy.cards.finance

import cn.nkpro.ts5.annotation.NkNote
import cn.nkpro.ts5.docengine.NkAbstractCard
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component;

// todo 待开发卡片
@Order(10001)
@NkNote("还款计划")
@Component("NkCardPaymentSchedule")
class NkCardPaymentSchedule extends NkAbstractCard<List,Map> {
}
