/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.components.financial.cards

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.co.easy.EasyCollection
import cn.nkpro.easis.co.easy.EasySingle
import cn.nkpro.easis.docengine.NkAbstractCard
import cn.nkpro.easis.docengine.model.DocDefIV
import cn.nkpro.easis.docengine.model.DocHV
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(10002)
@NkNote("账单")
@Component("NkCardBills")
class NkCardBills extends NkAbstractCard<List<BillI>,BillDef> {

    @Override
    List<BillI> afterCreate(DocHV doc, DocHV preDoc, List<BillI> data, DocDefIV defIV, BillDef d) {
        apply(doc, data, d)
        return super.afterCreate(doc, preDoc, data, defIV, d) as List
    }

    @Override
    List<BillI> calculate(DocHV doc, List<BillI> data, DocDefIV defIV, BillDef d, boolean isTrigger, Object options) {
        apply(doc, data, d)
        return super.calculate(doc, data, defIV, d, isTrigger, options) as List<BillI>
    }

    @Override
    List<BillI> beforeUpdate(DocHV doc, List<BillI> data, List<BillI> original, DocDefIV defIV, BillDef d) {

        // 判断激活条件
        boolean active = false
        if(d.activeSpEL){
            active = spELManager.invoke(d.activeSpEL, doc) as Boolean
        }

        // 激活行项目
        data.forEach({i->i.state = active?1:0})

        return super.beforeUpdate(doc, data, original, defIV, d) as List
    }

    void apply(DocHV doc, List<BillI> data, BillDef d){

        def context = spELManager.createContext(doc)

        // 将所有账单项目标记为废弃
        data.forEach({i->i.discard=1})

        if(d.collectSpEL){
            def invoke = spELManager.invoke(d.collectSpEL, context)
            assert invoke instanceof List

            invoke.forEach({i->
                if(i instanceof List){
                    i.forEach({ii->
                        def single = EasySingle.from(ii)
                        appendBill(data,
                                single.get("billType"),
                                single.get("expireDate"),
                                single.get("amount")
                        )
                    })
                }else{
                    def single = EasySingle.from(i)
                    appendBill(data,
                            single.get("billType"),
                            single.get("expireDate"),
                            single.get("amount")
                    )
                }
            })
        }


        if(d.paymentCardKey){
            def payments = doc.getData()["payment"]
            if(payments){

                EasyCollection coll = EasyCollection.from(payments)

                coll.forEach({ single ->

                    Long expireDate = single.get("expireDate")
                    appendBill(data, "本金", expireDate, single.get("principal"))
                    appendBill(data, "利息", expireDate, single.get("interest"))
                    appendBill(data, "费用", expireDate, single.get("fee"))
                })
            }

            // 将已收有值的数据强制标记为启用，避免已收丢失
            data.forEach({i->
                i.discard = i.received>0 && i.discard==1 ? 0 : 1
            })

            data.sort({ a, b ->
                if(a.expireDate==null)
                    return -1
                if(b.expireDate==null)
                    return 1
                return a.expireDate <=> b.expireDate
            })
        }
    }

    static void appendBill(List<BillI> data, String billType, Long expireDate, Double amount){

        if(!amount || amount==0)
            return

        BillI exists = data.stream()
                .find {i->i.billType ==  billType && i.expireDate == expireDate} as BillI

        if(!exists){

            exists = new BillI()
            exists.billType = billType
            exists.expireDate = expireDate
            exists.amount = 0
            exists.received = 0
            exists.receivable = 0

            data.add(exists)
        }

        exists.amount = amount
        exists.receivable = exists.amount - exists.received

        // 启用
        exists.discard = 0
    }

    static class BillDef{
        String collectSpEL
        String activeSpEL
        String paymentCardKey
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class BillI{
        String billType
        Long expireDate
        Double amount
        Double received
        Double receivable
        Integer state   // 0 未激活 1 激活
        Integer discard // 0 正常 1 过期的、失效的
    }
}
