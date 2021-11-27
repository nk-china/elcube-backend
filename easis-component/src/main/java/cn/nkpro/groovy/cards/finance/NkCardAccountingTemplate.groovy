package cn.nkpro.groovy.cards.finance

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.docengine.NkAbstractCard
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

// todo 待开发卡片
@Order(100010)
@NkNote("财务凭证模版")
@Component("NkCardAccountingTemplate")
class NkCardAccountingTemplate extends NkAbstractCard<Map,Map> {
}
