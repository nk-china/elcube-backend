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
import cn.nkpro.elcube.co.spel.NkSpELManager
import cn.nkpro.elcube.data.redis.RedisSupport
import cn.nkpro.elcube.docengine.NkAbstractCard
import cn.nkpro.elcube.docengine.NkDocEngine
import cn.nkpro.elcube.docengine.gen.*
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
import cn.nkpro.elcube.utils.BeanUtilz
import cn.nkpro.elcube.utils.DateTimeUtilz
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import org.apache.ibatis.session.RowBounds
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

import java.util.stream.Collectors

@Order(10003)
@NkNote("账单偿还")
@Component("NkCardRepayment")
class NkCardRepayment extends NkAbstractCard<List<DocIReceivedI>,Def> {


    @Autowired
    private DocIBillMapper billMapper
    @Autowired
    private DocIReceivedMapper receivedMapper
    @Autowired
    private JdbcTemplate jdbcTemplate
    @Autowired
    private NkSpELManager spELManager
    @Autowired
    private NkDocEngine docEngine
    @Autowired
    private RedisSupport redisSupport

    // todo 待功能测试稳定后删除
    //private static String sql =
    //        "update nk_doc_i_bill " +
    //                "   set received = received + ? , " +
    //                "       receivable = receivable - ? " +
    //                " where doc_id = ? " +
    //                "   and card_key = ? " +
    //                "   and bill_type = ? " +
    //                "   and expire_date = ? " +
    //                "   and bill_partner_id = ?"

    @Override
    List<DocIReceivedI> afterCreate(DocHV doc, DocHV preDoc, List<DocIReceivedI> data, DocDefIV defIV, Def d) {
        return super.afterCreate(doc, preDoc, data, defIV, d) as List
    }

    @Override
    List<DocIReceivedI> afterGetData(DocHV doc, List<DocIReceivedI> data, DocDefIV defIV, Def d) {

        DocIReceivedExample example = new DocIReceivedExample()
        example.createCriteria()
                .andDocIdEqualTo(doc.getDocId())
        example.setOrderByClause("ORDER_BY")

        return BeanUtilz.copyFromList(
                receivedMapper.selectByExample(example),
                DocIReceivedI.class,
                { r -> r.checked = true}
        ) as List<DocIReceivedI>
    }

    @Override
    List<DocIReceivedI> calculate(DocHV doc, List<DocIReceivedI> data, DocDefIV defIV, Def d, boolean isTrigger, Object options) {

        if("checkChange"!=options){
            this.init(doc, data, d)
        }else{
            // 用户通过UI界面手动调整分解明细时重新计算本次实收
            def context = spELManager.createContext(doc)
            double  availableAmount = spELManager.invoke(d.availableAmountSpEL, context) as double
            double  repayment       = 0d
            if(availableAmount){
                data.forEach({ received ->
                    if(received.checked){

                        received.currReceived = Math.min(received.receivable, availableAmount)
                        availableAmount -= received.currReceived
                        repayment       += received.currReceived
                    }else{
                        received.currReceived = 0d
                    }
                })
            }
            // 回填剩余未分解金额
            if(d.repaymentSpEL){
                spELManager.invoke(d.repaymentSpEL+"="+repayment,context)
            }
        }

        return super.calculate(doc, data, defIV, d, isTrigger, options) as List
    }

    /**
     * 初始化分解明细，在单据创建时执行
     * @param doc
     * @param data
     * @param d
     */
    void init(DocHV doc, List<DocIReceivedI> data, Def d){

        data.clear()

        if(doc.partnerId &&
                d.repaymentRuleGroup &&
                d.availableAmountSpEL &&
                d.accountDateSpEL
        ){

            def context = spELManager.createContext(doc)

            double  availableAmount = spELManager.invoke(d.availableAmountSpEL, context) as double
            double  repayment       = 0d

            if(availableAmount){
                // 如果可用金额>0 开始分解

                long    accountDate = spELManager.invoke(d.accountDateSpEL, context) as long
                def     ruleGroups  = JSON.parseObject(d.repaymentRuleGroup,new TypeReference<List<List<Rule>>>(){})

                context.setVariable("\$accountDate", accountDate)

                // 循环分解规则
                ruleGroups.forEach({ ruleGroup ->

                    List<String> billTypes = null
                    Map<String,String> billTypeConditions = new HashMap()
                    if(ruleGroup.size()){
                        billTypes = ruleGroup.stream()
                                .map({rule->rule.billType})
                                .collect(Collectors.toList())

                        ruleGroup.forEach({rule->
                            if(rule.condition)
                                billTypeConditions.put(rule.billType,rule.condition)
                        })
                    }


                    int  offset = 0

                    DocIBillExample example = new DocIBillExample()
                    DocIBillExample.Criteria c = example.createCriteria()
                            .andBillPartnerIdEqualTo(doc.partnerId)
                            .andReceivableGreaterThanOrEqualTo(0.01)
                            .andStateEqualTo(1)
                            .andDiscardEqualTo(0)

                    if(billTypes!=null)
                        c.andBillTypeIn(billTypes)

                    example.setOrderByClause("EXPIRE_DATE ASC")

                    Map<String,DocHV> docs = new HashMap<>()

                    while(availableAmount>0){
                        List<DocIBill> bills = billMapper.selectByExample(example, new RowBounds(offset,20))
                        offset += 20

                        bills = bills.stream()
                                .filter({bill ->
                                    if(billTypeConditions.containsKey(bill.billType)){
                                        String conditionSpEL = billTypeConditions.get(bill.billType)
                                        context.setVariable("bill", bill)
                                        return spELManager.invoke(conditionSpEL, context) as boolean
                                    }
                                    return true
                                })
                                .collect(Collectors.toList())

                        if(bills.size()==0)
                            break

                        bills.forEach({ bill ->

                            if(availableAmount>0){

                                DocIReceivedI received = new DocIReceivedI()
                                // 原始账单信息
                                received.targetDocId = bill.docId
                                received.cardKey     = bill.cardKey
                                received.amount      = bill.amount
                                received.billType    = bill.billType
                                received.expireDate  = bill.expireDate
                                received.received    = bill.received
                                received.receivable  = bill.receivable
                                received.docNumber   = docs.computeIfAbsent(bill.docId, { docId ->
                                    docEngine.detail(docId)
                                }).docNumber


                                // 核销信息
                                received.accountDate  = accountDate
                                received.currReceived = Math.min(received.receivable, availableAmount)

                                received.checked     = true

                                availableAmount -= received.currReceived
                                repayment       += received.currReceived

                                data.add(received)
                            }
                        })
                    }


                })
                // 结束循环分解规则
                // 结束分解

            }

            // 回填剩余未分解金额
            if(d.repaymentSpEL){
                spELManager.invoke(d.repaymentSpEL+"="+repayment,context)
            }
        }
    }

    @Override
    List<DocIReceivedI> beforeUpdate(DocHV doc, List<DocIReceivedI> data, List<DocIReceivedI> original, DocDefIV defIV, Def d) {

        // 是否使分解生效
        boolean active = d.activeSpEL ? spELManager.invoke(d.activeSpEL, doc) as boolean : false
        // 初始化一个当前时间，避免资源浪费
        long    now    = DateTimeUtilz.nowSeconds()

        // todo 待功能测试稳定后删除
        // List<Object[]> args = new ArrayList<>()

        // 移除没有勾选的分解项目
        data.removeIf({ received -> !received.checked})

        // 更新时，需要锁定被偿还的账单单据
        Set<String> docIds = data.stream().map({ i -> i.targetDocId }).distinct().collect(Collectors.toSet())
        if(original){
            docIds.addAll(original.stream().map({ i -> i.targetDocId }).distinct().collect(Collectors.toSet()))
        }
        // 开始保存分解明细

        // todo 待功能测试稳定后删除
        // Map<String,String> locked = new HashMap<>()
        // if(docIds.size()>0){
        //     docIds.forEach({ id ->
        //         locked.put(id,redisSupport.lock(id, 600))
        //     })
        // }

        // try{

        // 创建一个集合，按支付表ID分组，保存被分解的应收明细，最后批量更新支付表
        Map<String,List<DocIReceivedI>> docUpgrades = new HashMap<>()

        // todo 待功能测试稳定后删除
        // 获取所有被分解的单据对象
        //locked.forEach({ key, value ->
        //    docs.put(key,docEngine.detail(key))
        //})

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
                // todo 待功能测试稳定后删除
                // 更新账单, 增加实收
                // args.add([received.currReceived,
                //           received.currReceived,
                //           received.targetDocId,
                //           received.cardKey,
                //           received.billType,
                //           received.expireDate,
                //           doc.partnerId
                // ] as Object[])

                // 分解生效为true，分解明细为false，那么本次分解为正向操作，增加账单的实收
                docUpgrades
                    .computeIfAbsent(received.targetDocId,{id->new ArrayList<>()})
                    .add(received)

            }else if(!active && state == 1){
                // todo 待功能测试稳定后删除
                // 更新账单，减少实收
                // args.add([-received.currReceived,
                //           -received.currReceived,
                //           received.targetDocId,
                //           received.cardKey,
                //           received.billType,
                //           received.expireDate,
                //           doc.partnerId
                // ] as Object[])

                // 分解生效为false，分解明细为true，那么本次分解为逆向操作，减少账单的实收
                // copy对象，对当前实收取反
                DocIReceivedI copied = BeanUtilz.copyFromObject(received, DocIReceivedI.class)
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

                        // todo 待功能测试稳定后删除
                        // args.add([-o.currReceived,
                        //           -o.currReceived,
                        //           o.targetDocId,
                        //           o.cardKey,
                        //           o.billType,
                        //           o.expireDate,
                        //           doc.partnerId
                        // ] as Object[])


                        // 如果原始数据的有效性为true，那分解为逆向操作，减少账单的实收
                        // copy对象，对当前实收取反
                        DocIReceivedI copied = BeanUtilz.copyFromObject(o, DocIReceivedI.class)
                        copied.currReceived = -copied.currReceived

                        docUpgrades.computeIfAbsent(o.targetDocId,{id->new ArrayList<>()})
                                .add(copied)
                    }
                }
            })
        }

        // todo 待功能测试稳定后删除
        //if(!args.isEmpty())
        //    jdbcTemplate.batchUpdate(sql, args)
        //locked.forEach({ id,value ->
        //    docEngine.doUpdate(id, "偿还-更新账单", { billDoc ->
        //        docEngine.calculate(billDoc,null,null)
        //    })
        //})

        if(!docUpgrades.isEmpty()){

        // todo 待功能测试稳定后删除
        // DocHV targetDoc = docs.get(o.targetDocId)
        //
        // EasySingle easyBill = targetDoc.fetchList(o.cardKey)
        //         .find({ e ->
        //             e.get("billType")==o.billType &&
        //                     e.get("expireDate")==o.expireDate &&
        //                     e.get("billPartnerId")==doc.partnerId
        //         })
        //
        // easyBill.set("received",  easyBill.get("received") as double - o.currReceived)
        // easyBill.set("receivable",easyBill.get("received") as double + o.currReceived)

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

        // todo 待功能测试稳定后删除
        // }finally{
        //     locked.forEach({ id,value ->
        //         redisSupport.unlock(id, value)
        //     })
        // }

        // 返回null，不使用docEngine保存数据
        return null
    }

    static class Def{
        String activeSpEL
        String availableAmountSpEL
        String accountDateSpEL
        String repaymentSpEL
        String repaymentRuleGroup
    }

    static class Rule{
        String billType
        String condition
    }

    static class DocIReceivedI extends DocIReceived{
        Boolean checked
    }
}
