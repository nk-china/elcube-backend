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
import cn.nkpro.elcube.co.easy.EasyCollection
import cn.nkpro.elcube.co.easy.EasySingle
import cn.nkpro.elcube.docengine.NkAbstractCard
import cn.nkpro.elcube.docengine.gen.DocIBill
import cn.nkpro.elcube.docengine.gen.DocIBillExample
import cn.nkpro.elcube.docengine.gen.DocIBillKey
import cn.nkpro.elcube.docengine.gen.DocIBillMapper
import cn.nkpro.elcube.docengine.gen.DocIReceived
import cn.nkpro.elcube.docengine.gen.DocIReceivedExample
import cn.nkpro.elcube.docengine.gen.DocIReceivedMapper
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
import cn.nkpro.elcube.utils.BeanUtilz
import cn.nkpro.elcube.utils.DateTimeUtilz
import com.alibaba.fastjson.JSON
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import java.math.RoundingMode
import java.util.stream.Collectors

@Order(10002)
@NkNote("账单")
@Component("NkCardBills")
class NkCardBills extends NkAbstractCard<List<DocIBill>,BillDef> {

    @Autowired
    private DocIBillMapper billMapper
    @Autowired
    private DocIReceivedMapper receivedMapper


    @Override
    List<DocIBill> afterCreate(DocHV doc, DocHV preDoc, List<DocIBill> data, DocDefIV defIV, BillDef d) {
        apply(doc, defIV, data, d)
        return super.afterCreate(doc, preDoc, data, defIV, d) as List
    }

    @Override
    List<DocIBill> calculate(DocHV doc, List<DocIBill> data, DocDefIV defIV, BillDef d, boolean isTrigger, Object options) {
        apply(doc, defIV, data, d)
        return super.calculate(doc, data, defIV, d, isTrigger, options) as List<DocIBill>
    }

    /**
     * 从数据库表中获取账单数据，不通过docEngine管理
     * @param doc
     * @param data
     * @param defIV
     * @param d
     * @return
     */
    @Override
    List<DocIBill> afterGetData(DocHV doc, List<DocIBill> data, DocDefIV defIV, BillDef d) {

        DocIBillExample example = new DocIBillExample()
        example.createCriteria()
                .andDocIdEqualTo(doc.docId)
                .andDiscardEqualTo(0)
        example.setOrderByClause("EXPIRE_DATE")

        return billMapper.selectByExample(example)
        //return super.afterGetData(doc, data, defIV, d) as List
    }

    /**
     * 不使用docEngine保存数据
     * @param doc
     * @param data
     * @param original
     * @param defIV
     * @param d
     * @return
     */
    @Override
    List<DocIBill> beforeUpdate(DocHV doc, List<DocIBill> data, List<DocIBill> original, DocDefIV defIV, BillDef d) {

        // 判断激活条件
        boolean active = d.activeSpEL && spELManager.invoke(d.activeSpEL, doc) as Boolean

        // 激活行项目
        data.forEach({i->i.state = active ? 1 : 0 })

        // 先对比再保存行项目
        if(original){

            data.forEach({i->

                def exists = original.find {o->
                    return o.expireDate == i.expireDate &&
                            o.billType == i.billType &&
                            o.cardKey == i.cardKey
                }

                if(exists){
                    if(!(exists.amount==i.amount&&
                            exists.received==i.received&&
                            exists.receivable==i.receivable&&
                            exists.billPartnerId==i.billPartnerId&&
                            exists.state==i.state&&
                            exists.discard==i.discard&&
                            exists.details==i.details&&
                            exists.updatedTime==i.updatedTime
                    )){
                        i.updatedTime = DateTimeUtilz.nowSeconds()
                        billMapper.updateByPrimaryKeySelective(i)
                    }
                }else{
                    i.updatedTime = DateTimeUtilz.nowSeconds()
                    billMapper.insert(i)
                }
            })

            original.forEach({o->
                def exists = data.find {i->
                    return o.expireDate == i.expireDate &&
                            o.billType == i.billType &&
                            o.cardKey == i.cardKey
                }
                if(!exists){
                    billMapper.deleteByPrimaryKey(o)
                }
            })
        }else{
            // 如果原始数据不存在，插入数据
            data.forEach({i->
                billMapper.insert(i)
            })
        }

        // 返回null，不使用docEngine保存数据
        return null
    }

    @Override
    Object call(DocHV doc, List<DocIBill> data, DocDefIV defIV, BillDef d, Object options) {

        def single = EasySingle.from(options)

        if(single.get("operator") == 'details'){

            DocIBillKey key = new DocIBillKey()
            key.setDocId(doc.getDocId())
            key.setCardKey(defIV.getCardKey())
            key.setBillPartnerId(single.get("billPartnerId"))
            key.setBillType(single.get("billType"))
            key.setExpireDate(single.get("expireDate"))

            return billMapper.selectByPrimaryKey(key)
        }

        if(single.get("operator") == 'repayments'){
            DocIReceivedExample example = new DocIReceivedExample()
            example.createCriteria()
                    .andTargetDocIdEqualTo(doc.getDocId())
                    .andCardKeyEqualTo(defIV.getCardKey())
                    .andBillTypeEqualTo(single.get("billType"))
                    .andExpireDateEqualTo(single.get("expireDate"))
                    .andBillPartnerIdEqualTo(single.get("billPartnerId"))
                    .andStateEqualTo(1)
            example.setOrderByClause("CREATED_TIME, ORDER_BY")

            return receivedMapper.selectByExample(example)
        }

        return null
    }


    /**
     * 创建账单
     * @param doc
     * @param data
     * @param d
     */
    void apply(DocHV doc, DocDefIV defIV, List<DocIBill> data, BillDef d){

        def context = spELManager.createContext(doc)

        // 先将所有账单项目标记为废弃
        data.forEach({i->i.discard=1})

        // 如果指定了账单表达式
        if(d.collectSpEL){
            def invoke = spELManager.invoke(d.collectSpEL, context)
            assert invoke instanceof List

            // 将账单表达式的结果数据添加到账单中
            invoke.forEach({i->
                if(i instanceof List){
                    i.forEach({ii->
                        def single = EasySingle.from(ii)
                        appendBill(doc, defIV,data,
                                single.get("billType"),
                                single.get("expireDate"),
                                single.get("amount")
                        )
                    })
                }else{
                    def single = EasySingle.from(i)
                    appendBill(doc, defIV,data,
                            single.get("billType"),
                            single.get("expireDate"),
                            single.get("amount")
                    )
                }
            })
        }

        // 如果指定了还款计划卡片
        if(d.paymentCardKey){
            def payments = doc.getData()["payment"]
            if(payments){

                // 将还款计划卡片的结果数据添加到账单中
                EasyCollection.from(payments).forEach({ single ->
                    Long expireDate = single.get("expireDate")
                    appendBill(doc, defIV, data, "本金", expireDate, single.get("principal"))
                    appendBill(doc, defIV, data, "利息", expireDate, single.get("interest"))
                    appendBill(doc, defIV, data, "费用", expireDate, single.get("fee"))
                })
            }

            // 因为账单卡片的数据会变更
            // 难免会出现变更以后实收大于应收的情况
            // 所以，将已收金额大于 0 的数据 强制标记为启用，避免已收丢失
            data.forEach({i->
                i.discard = i.received>0 && i.discard==1 ? 0 : i.discard
            })

            // 按到期日期排序
            data.sort({ a, b ->
                if(a.expireDate==null)
                    return -1
                if(b.expireDate==null)
                    return 1
                return a.expireDate <=> b.expireDate
            })
        }

        // 计算罚息
        calcOverdue(doc, defIV, data, d, null, null)

        // 按到期日期排序
        data.sort({ a, b -> (a.expireDate <=> b.expireDate) })
    }

    /**
     *
     * 根据计算日期，重新计算滞纳金
     *
     *
     * <p>一般情况下，滞纳金每天0点需要计算一次，计算日期与截止日期均为系统日期当天
     * <p>在滞纳金重算情况下，计算日期为指定的日期，这时，将重新计算 计算日期到截止日期 这个时间区间的滞纳金
     * <p>滞纳金重算应用场景主要为资金核销后，需要根据资金到账日期，实时更新还款计划的滞纳金情况
     *
     * @param data
     * @param d
     * @param startDate 计算日期（即重算开始日期）默认为需要计算滞纳金的第一条应收日期
     * @param endDate   截止日期（一般为系统日期today）默认为系统日期
     */
    void calcOverdue(DocHV doc, DocDefIV defIV, List<DocIBill> data, BillDef d, Long startDate, Long endDate){

        if(data.size() == 0 || d.overdueBillDefs==null || d.overdueBillDefs.size() == 0)
            return

        // 1、数据准备阶段

        startDate = startDate?:data.stream()
                                    .filter({item->d.overdueBillDefs.contains(item.billType)})
                                    .map({item->item.expireDate})
                                    .findFirst()
                                    .orElse(DateTimeUtilz.todaySeconds())
        endDate   = endDate  ?:DateTimeUtilz.todaySeconds()

        // 需要计算违约金的账单项目
        def calculativeBills = data.stream()
                .filter({ item ->

                    // 如果不违约金类型中
                    if(!d.overdueBillDefs.contains(item.billType)){
                        return false
                    }

                    // 如果是违约金项目本身
                    if(item.billType == d.overdueBillType){
                        // 过滤调计算日期之后的项目，因为计算日期到截止日期之间的项目，就是本次需要计算的结果
                        return item.expireDate < startDate
                    }

                    // 过滤调截止日期之后的项目
                    return item.expireDate <= endDate
                })
                .collect(Collectors.toList())

        // 已经存在的违约金的账单项目
        def existsBreachBills = calculativeBills.stream()
                .filter({item -> item.billType == d.overdueBillType})
                .collect(Collectors.toList())

        // 如果没有找到任何需要计算违约金的账单，则清空所有的违约金项目
        if(calculativeBills.isEmpty()){
            existsBreachBills.forEach({item -> item.discard = 1})
            return
        }

        // 查询生效的分解明细
        DocIReceivedExample example = new DocIReceivedExample()
        example.createCriteria()
                .andTargetDocIdEqualTo(doc.getDocId())
                .andExpireDateLessThanOrEqualTo(endDate)
                .andStateEqualTo(1)
        example.setOrderByClause("ACCOUNT_DATE")
        List<DocIReceived> received = receivedMapper.selectByExample(example)

        // 2、循环计算阶段
        log.info("开始计算滞纳金")
        log.info("开始日期:{}", DateTimeUtilz.format(startDate,"yyyy-MM-dd"))
        log.info("截止日期:{}", DateTimeUtilz.format(endDate,  "yyyy-MM-dd"))
        log.info("滞纳金应收项目:{}", d.overdueBillDefs)
        log.info("滞纳金账单项目:{}", d.overdueBillType)
        log.info("滞纳金日利率:{}%", d.overdueBillRate)
        log.info("Loop:")

        while(startDate <= endDate){
            // 开始循环计算每一天的罚息
            // startDate 为循环的当前日

            def formattedDate = DateTimeUtilz.format(startDate,"yyyy-MM-dd")

            log.info("-{}",formattedDate)

            double subtotal = 0d
            List<OverdueDetail> details = new ArrayList<>()

            // 每天对所有的应收项目计算一次
            calculativeBills.stream()
                    // 到期日期 <= 当前计算日期
                    // 注意：当前计算日期当天是需要计算罚息的，但是资金分解时，不分解到账日期当天的罚息
                    .filter({ item -> item.expireDate <= startDate })
                    .forEach({ item ->

                        // 条目的应收
                        double baseAmount = item.amount

                        // 截止到计算日期的已收情况
                        double receivedAmount = received.stream()
                                .filter({ r ->
                                    r.billType == item.billType &&
                                            r.expireDate == item.expireDate &&
                                            r.accountDate <= startDate
                                })
                                .collect(Collectors.summingDouble({ r -> r.currReceived }))

                        double receivable = baseAmount - receivedAmount

                        if(receivable>0){

                            // 计算出滞纳金
                            double overdueAmount = (baseAmount - receivedAmount) * d.overdueBillRate / 100

                            OverdueDetail detail = BeanUtilz.copyFromObject(item, OverdueDetail.class)
                            detail.received = receivedAmount
                            detail.receivable = receivable
                            detail.overdueAmount = overdueAmount

                            details.add(detail)

                            // 四舍五入后计入小计
                            subtotal += BigDecimal.valueOf(overdueAmount).setScale(2, RoundingMode.HALF_EVEN).doubleValue()
                        }
                    })
            log.info("-{}: overdue fine = {}",formattedDate,subtotal)

            // 添加一个账单项目
            DocIBill overdueBill = appendBill(doc, defIV, data, d.overdueBillType, startDate, subtotal)

            if(overdueBill){
                overdueBill.setDetails(JSON.toJSONString(details))

                // 如果需要计算复利
                if(d.overdueBillDefs.contains(d.overdueBillType))
                    calculativeBills.add(overdueBill)
            }

            // 起始日期 + 1
            startDate = DateTimeUtilz.dateAdd(startDate,1)
        }
        log.info("Loop End")

        // 3、清理无效的滞纳金数据
        data.removeIf({item ->
                item.billType == d.overdueBillType &&   // 罚息类型
                item.expireDate > endDate &&            // 到期日期在endDate之后
                item.received == 0                      // 实收为0
        })

    }

    /**
     * 创建账单项目
     * @param doc
     * @param data
     * @param billType
     * @param expireDate
     * @param amount
     */
    static DocIBill appendBill(DocHV doc, DocDefIV defIV, List<DocIBill> data, String billType, Long expireDate, Double amount){

        DocIBill exists = data.stream()
                .find {i->i.billType ==  billType && i.expireDate == expireDate} as DocIBill

        if(!exists){

            if(!amount || amount==0)
                return null

            exists = new DocIBill()
            exists.docId = doc.docId
            exists.cardKey = defIV.cardKey
            exists.billType = billType
            exists.billPartnerId = doc.partnerId
            exists.expireDate = expireDate
            exists.received = 0

            data.add(exists)
        }

        exists.amount = amount
        exists.receivable = exists.amount - exists.received

        // 启用
        exists.discard = 0

        return exists
    }

    static class BillDef{
        String collectSpEL
        String activeSpEL
        String paymentCardKey

        String overdueBillType
        Double overdueBillRate
        List<String> overdueBillDefs

        @SuppressWarnings("unused")
        String viewDefs
    }

    @SuppressWarnings("unused")
    static class OverdueDetail{
        String billType
        Long expireDate
        Double amount
        Double received
        Double receivable
        Double overdueAmount
    }
}
