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

// todo 待开发卡片
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

    private static String sql =
            "update nk_doc_i_bill " +
                    "   set received = received + ? , " +
                    "       receivable = receivable - ? " +
                    " where doc_id = ? " +
                    "   and card_key = ? " +
                    "   and bill_type = ? " +
                    "   and expire_date = ? " +
                    "   and bill_partner_id = ?"

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

        if("checkChange"==options){
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
            if(d.repaymentSpEL){
                spELManager.invoke(d.repaymentSpEL+"="+repayment,context)
            }
        }else{
            this.init(doc, data, d)
        }

        return super.calculate(doc, data, defIV, d, isTrigger, options) as List
    }

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

            if(d.repaymentSpEL){
                spELManager.invoke(d.repaymentSpEL+"="+repayment,context)
            }
        }
    }

    @Override
    List<DocIReceivedI> beforeUpdate(DocHV doc, List<DocIReceivedI> data, List<DocIReceivedI> original, DocDefIV defIV, Def d) {


        boolean active = d.activeSpEL ? spELManager.invoke(d.activeSpEL, doc) as boolean : false
        long    now    = DateTimeUtilz.nowSeconds()

        List<Object[]> args = new ArrayList<>()

        data.removeIf({ received -> !received.checked})


        // 更新时，需要锁定被偿还的账单单据
        Set<String> docIds = data.stream().map({ i -> i.getDocId() }).distinct().collect(Collectors.toSet())
        if(original){
            docIds.addAll(original.stream().map({ i -> i.getDocId() }).distinct().collect(Collectors.toSet()))
        }
        Map<String,String> locked = new HashMap<>()
        if(docIds.size()>0){
            docIds.forEach({ id ->
                locked.put(id,redisSupport.lock(id, 600))
            })
        }
        try{

            data.forEach({ received ->

                def state = received.state ?: 0

                // 系统字段
                received.docId       = doc.docId
                received.updatedTime = now
                received.state       = active ? 1 : 0
                received.orderBy     = data.indexOf(received)

                if(original && original.find {
                    o-> o.billType==received.billType &&
                            o.targetDocId == received.targetDocId &&
                            o.expireDate == received.expireDate
                }){
                    receivedMapper.updateByPrimaryKey(received)
                }else{
                    received.createdTime = now
                    receivedMapper.insert(received)
                }

                if(active && state == 0){
                    // 更新账单, 增加实收
                    args.add([received.currReceived,
                              received.currReceived,
                              received.targetDocId,
                              received.cardKey,
                              received.billType,
                              received.expireDate,
                              doc.partnerId
                    ] as Object[])
                }else if(!active && state == 1){
                    // 更新账单，减少实收
                    args.add([-received.currReceived,
                              -received.currReceived,
                              received.targetDocId,
                              received.cardKey,
                              received.billType,
                              received.expireDate,
                              doc.partnerId
                    ] as Object[])
                }
            })

            if(original){
                original.forEach({ o ->
                    if(!data.find { received->  o.billType==received.billType &&
                            o.targetDocId == received.targetDocId &&
                            o.expireDate == received.expireDate}){
                        receivedMapper.deleteByPrimaryKey(o)
                        if(o.state == 1){
                            args.add([-o.currReceived,
                                      -o.currReceived,
                                      o.targetDocId,
                                      o.cardKey,
                                      o.billType,
                                      o.expireDate,
                                      doc.partnerId
                            ] as Object[])
                        }
                    }
                })
            }

            if(!args.isEmpty())
                jdbcTemplate.batchUpdate(sql, args)
        }finally{
            locked.forEach({ id,value ->
                redisSupport.unlock(id, value)
            })
        }

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
