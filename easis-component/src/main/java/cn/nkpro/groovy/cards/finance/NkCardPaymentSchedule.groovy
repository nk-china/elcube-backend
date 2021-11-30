package cn.nkpro.groovy.cards.finance

import cn.nkpro.easis.annotation.NkNote
import cn.nkpro.easis.co.NkApplyCSO
import cn.nkpro.easis.co.NkCustomObjectManager
import cn.nkpro.easis.docengine.NkAbstractCard
import cn.nkpro.easis.docengine.model.DocDefIV
import cn.nkpro.easis.docengine.model.DocHV
import cn.nkpro.easis.utils.BeanUtilz
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import java.util.stream.Collectors

// todo 待开发卡片
@Order(10001)
@NkNote("还款计划")
@Component("NkCardPaymentSchedule")
class NkCardPaymentSchedule extends NkAbstractCard<List<PaymentI>,Def> {

    @Autowired
    NkCustomObjectManager customObjectManager

    @Override
    List<PaymentI> calculate(DocHV doc, List<PaymentI> data, DocDefIV defIV, Def d, boolean isTrigger, Object options) {

        if(StringUtils.isNoneBlank(d.getMtSpEL())){
            def context = spELManager.createContext(doc)

            String  mt = spELManager.invoke(d.getMtSpEL(), context)
            def params  = spELManager.invoke(d.getMtOptions(), context) as Map

            def apply = customObjectManager.getCustomObject(mt, NkApplyCSO.class)
                .apply(params) as List

            if(apply!=null){
                data = apply.stream()
                    .map({ item ->
                        return BeanUtilz.copyFromObject(item, PaymentI.class)
                    }).collect(Collectors.toList()) as List<PaymentI>
            }else{
                data = Collections.emptyList()
            }

        }

        return super.calculate(doc, data, defIV, d, isTrigger, options) as List
    }

    static class Def{
        String mtSpEL   // 计算公式
        String mtOptions
//        String pvSpEL   // 现值
//        String irSpEL   // 年利率
//        String npSpEL   // 期间数
//        String pfSpEL   // 频率（次/年）
//        String fvSpEL   // 终值
//        String dtSpEL   // 计息日期
    }

    static class PaymentI{
        Integer period
        Long expireDate
        Double pay
        Double principal
        Double interest
        Double residual
        Double fee
        String remark
    }
}
