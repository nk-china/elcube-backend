package cn.nkpro.easis.components.financial.cards


import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

// todo 待开发卡片
@Order(10002)
@NkNote("账单")
@Component("NkCardBills")
class NkCardBills extends NkAbstractCard<Map,Map> {
}
