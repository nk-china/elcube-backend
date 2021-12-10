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
import cn.nkpro.elcube.docengine.gen.DocIBillMapper
import cn.nkpro.elcube.docengine.model.DocDefIV
import cn.nkpro.elcube.docengine.model.DocHV
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(10002)
@NkNote("账单")
@Component("NkCardBills")
class NkCardBills extends NkAbstractCard<List<DocIBill>,BillDef> {

    @Autowired
    private DocIBillMapper billMapper

    @Override
    List<DocIBill> afterCreate(DocHV doc, DocHV preDoc, List<DocIBill> data, DocDefIV defIV, BillDef d) {
        apply(doc, data, d)
        return super.afterCreate(doc, preDoc, data, defIV, d) as List
    }

    @Override
    List<DocIBill> calculate(DocHV doc, List<DocIBill> data, DocDefIV defIV, BillDef d, boolean isTrigger, Object options) {
        apply(doc, data, d)
        return super.calculate(doc, data, defIV, d, isTrigger, options) as List<DocIBill>
    }

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

    @Override
    List<DocIBill> beforeUpdate(DocHV doc, List<DocIBill> data, List<DocIBill> original, DocDefIV defIV, BillDef d) {

        // 判断激活条件
        boolean active = false
        if(d.activeSpEL){
            active = spELManager.invoke(d.activeSpEL, doc) as Boolean
        }

        // 激活行项目
        data.forEach({i->i.state = active?1:0})

        data.forEach({i->

            if(billMapper.selectByPrimaryKey(i)==null){
                billMapper.insert(i)
            }else{
                billMapper.updateByPrimaryKey(i)
            }
        })

        // 返回null，不使用docEngine保存数据
        return null
    }

    void apply(DocHV doc, List<DocIBill> data, BillDef d){

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
                        appendBill(doc,data,
                                single.get("billType"),
                                single.get("expireDate"),
                                single.get("amount")
                        )
                    })
                }else{
                    def single = EasySingle.from(i)
                    appendBill(doc,data,
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
                    appendBill(doc, data, "本金", expireDate, single.get("principal"))
                    appendBill(doc, data, "利息", expireDate, single.get("interest"))
                    appendBill(doc, data, "费用", expireDate, single.get("fee"))
                })
            }

            // 将已收有值的数据强制标记为启用，避免已收丢失
            data.forEach({i->
                i.discard = i.received>0 && i.discard==1 ? 0 : i.discard
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

    static void appendBill(DocHV doc, List<DocIBill> data, String billType, Long expireDate, Double amount){

        if(!amount || amount==0)
            return

        DocIBill exists = data.stream()
                .find {i->i.billType ==  billType && i.expireDate == expireDate} as DocIBill

        if(!exists){

            exists = new DocIBill()
            exists.docId = doc.docId
            exists.billType = billType
            exists.billPartnerId = doc.partnerId
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
}
