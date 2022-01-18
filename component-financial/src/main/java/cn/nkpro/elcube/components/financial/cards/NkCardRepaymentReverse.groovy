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
package cn.nkpro.elcube.components.financial.cards

import cn.nkpro.elcube.annotation.NkNote
import cn.nkpro.elcube.co.easy.EasySingle
import cn.nkpro.elcube.docengine.NkAbstractCard
import cn.nkpro.elcube.docengine.NkDocEngine
import cn.nkpro.elcube.docengine.gen.DocIReceived
import cn.nkpro.elcube.docengine.gen.DocIReceivedExample
import cn.nkpro.elcube.docengine.gen.DocIReceivedMapper
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
import cn.nkpro.elcube.utils.BeanUtilz
import cn.nkpro.elcube.utils.DateTimeUtilz
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import java.util.stream.Collectors

// todo 待开发卡片
@Order(10004)
@NkNote("账单红冲")
@Component("NkCardRepaymentReverse")
class NkCardRepaymentReverse extends NkAbstractCard<List<DocIReceived>,Def> {

    @Autowired
    private NkDocEngine docEngine
    @Autowired
    private DocIReceivedMapper receivedMapper

    @Override
    List<DocIReceived> afterCreate(DocHV doc, DocHV preDoc, List<DocIReceived> data, DocDefIV defIV, Def d) {
        data = apply(doc, preDoc, d)
        return super.afterCreate(doc, preDoc, data, defIV, d) as List
    }

    @Override
    List<DocIReceived> afterGetData(DocHV doc, List<DocIReceived> data, DocDefIV defIV, Def d) {

        DocIReceivedExample example = new DocIReceivedExample()
        example.createCriteria()
                .andDocIdEqualTo(doc.getDocId())
        example.setOrderByClause("ORDER_BY")

        return receivedMapper.selectByExample(example)
    }

    @Override
    List<DocIReceived> calculate(DocHV doc, List<DocIReceived> data, DocDefIV defIV, Def d, boolean isTrigger, Object options) {
        if("redo" == options){
            if(doc.preDocId){
                return apply(doc, docEngine.detail(doc.preDocId), d)
            }
            return Collections.emptyList()
        }
        return super.calculate(doc, data, defIV, d, isTrigger, options) as List
    }

    List<DocIReceived> apply(DocHV doc, DocHV preDoc, Def d){

        if(d.repaymentKey){
            List<DocIReceived> data = BeanUtilz.copyFromList(preDoc.getData().get(d.repaymentKey) as List<?>, DocIReceived.class, { row ->
                row.received        = row.currReceived
                row.receivable      = null
                row.currReceived    = -row.currReceived
                row.state           = 0
            }) as List<DocIReceived>

            if(d.balanceSpEL){
                double balance = - data.sum({ e -> e.currReceived })
                def context = spELManager.createContext(doc)
                spELManager.invoke(d.balanceSpEL + "=" + balance, context)
            }
            return data
        }

        return Collections.emptyList()
    }

    @Override
    List<DocIReceived> beforeUpdate(DocHV doc, List<DocIReceived> data, List<DocIReceived> original, DocDefIV defIV, Def d) {

        // 是否使分解生效
        boolean active = d.activeSpEL ? spELManager.invoke(d.activeSpEL, doc) as boolean : false
        // 初始化一个当前时间，避免资源浪费
        long    now    = DateTimeUtilz.nowSeconds()

        // 移除没有勾选的分解项目 冲红不需要
        // data.removeIf({ received -> !received.checked})

        // 更新时，需要锁定被偿还的账单单据
        Set<String> docIds = data.stream().map({ i -> i.targetDocId }).distinct().collect(Collectors.toSet())
        if(original){
            docIds.addAll(original.stream().map({ i -> i.targetDocId }).distinct().collect(Collectors.toSet()))
        }
        // 开始保存分解明细

        // 创建一个集合，按支付表ID分组，保存被分解的应收明细，最后批量更新支付表
        Map<String,List<DocIReceived>> docUpgrades = new HashMap<>()

        // 开始循环分解明细
        data.forEach({ received ->

            def state = received.state ?: 0

            // 系统字段
            received.docId         = doc.docId
            received.updatedTime   = now
            received.state         = active ? 1 : 0
            received.orderBy       = data.indexOf(received)
            received.billPartnerId = doc.partnerId

            // 根据原始数据判断 update 还是 insert
            if(original && original.find {
                o-> o.billType==received.billType &&
                        o.targetDocId == received.targetDocId &&
                        o.expireDate == received.expireDate
            }){
                receivedMapper.updateByPrimaryKeySelective(received)
            }else{
                received.createdTime = now
                receivedMapper.insert(received)
            }

            // 如果 当前分解明细的状态 与 当前计算出来的"分解生效" 不一致，就需要更新账单对应的单据
            if(active && state == 0){

                // 分解生效为true，分解明细为false，那么本次分解为正向操作，增加账单的实收
                docUpgrades
                        .computeIfAbsent(received.targetDocId,{id->new ArrayList<>()})
                        .add(received)

            }else if(!active && state == 1){

                // 分解生效为false，分解明细为true，那么本次分解为逆向操作，减少账单的实收
                // copy对象，对当前实收取反
                DocIReceived copied = BeanUtilz.copyFromObject(received, DocIReceived.class)
                copied.currReceived = -copied.currReceived

                docUpgrades
                        .computeIfAbsent(received.targetDocId,{id->new ArrayList<>()})
                        .add(copied)
            }
        })

        if(original){
            original.forEach({ o ->
                if(!data.find { received->  o.billType==received.billType &&
                        o.targetDocId == received.targetDocId &&
                        o.expireDate == received.expireDate}){

                    // 如果原始数据存在，当前数据不存在，就删除记录

                    receivedMapper.deleteByPrimaryKey(o)

                    if(o.state == 1){


                        // 如果原始数据的有效性为true，那分解为逆向操作，减少账单的实收
                        // copy对象，对当前实收取反
                        DocIReceived copied = BeanUtilz.copyFromObject(o, DocIReceived.class)
                        copied.currReceived = -copied.currReceived

                        docUpgrades.computeIfAbsent(o.targetDocId,{id->new ArrayList<>()})
                                .add(copied)
                    }
                }
            })
        }

        if(!docUpgrades.isEmpty()){

            // 批量更新支付表单据
            docUpgrades.forEach({ billDocId, upgradeReceived ->
                docEngine.doUpdate(billDocId, "账单偿还" , { billDoc ->
                    upgradeReceived.forEach({ received ->
                        EasySingle easyBill = billDoc.fetchList(received.cardKey)
                                .find({ e ->
                                    e.get("billType")==received.billType &&
                                            e.get("expireDate")==received.expireDate &&
                                            e.get("billPartnerId")==doc.partnerId
                                })

                        easyBill.set("received",  (easyBill.get("received") as double) + received.currReceived)
                        easyBill.set("receivable",(easyBill.get("received") as double) - received.currReceived)
                    })

                    // 调用一次支付表的计算方法，使其重算罚息
                    docEngine.calculate(billDoc, null, null)
                })
            })
        }

        // 返回null，不使用docEngine保存数据
        return null
    }

    static class Def{
        String activeSpEL = "docState=='S002'||docState=='S003'"
        String repaymentKey = "repayment"
        String balanceSpEL = "data.summary.balance"
    }
}
