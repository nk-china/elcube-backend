package cn.nkpro.groovy.cards.finance

import cn.nkpro.easis.co.NkAbstractApplyCSO
import cn.nkpro.easis.co.easy.EasySingle
import cn.nkpro.easis.co.spel.NkSpELManager
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.math.RoundingMode

@Component("NkFinanceMethod02")
class NkFinanceMethod02 extends NkAbstractApplyCSO {

    @Autowired
    NkSpELManager spELManager

    @Override
    Object apply(Object options) throws Exception {

        def single = EasySingle.from(options)
        def mfOptions = single.get("options")

        def context = spELManager.createContext(single.get("doc"))
        def params  = spELManager.invoke(mfOptions as String, context) as Map

        Double  ir = params.get("irSpEL") as Double
        Integer np = params.get("npSpEL") as Integer
        Integer pf = params.get("pfSpEL") as Integer
        Double  pv = params.get("pvSpEL") as Double
        Double  fv = params.get("fvSpEL") as Double
        Long    dt = params.get("dtSpEL") as Long

        if (ir != null && np != null && pf != null && pv != null && fv != null && dt != null){

            double residual = pv

            Calendar calendar = Calendar.getInstance()
            calendar.setTimeInMillis(dt*1000)

            // 期间利率 = 年利率 / 付款频率
            Double ip = ir / pf

            // 付款间隔 单位月
            int step = (int) (12 / pf)

            List<PaymentI> list = new ArrayList<>()
            PaymentI item
            for(int i=0;i<np;i++){


                calendar.add(Calendar.MONTH, step)
                item = new PaymentI()
                item.setPeriod(i+1)
                item.setExpireDate(calendar.getTimeInMillis()/1000 as Long)
                item.setInterest(doubleValue(residual*ip))
                item.setPrincipal(doubleValue(pv/np))
                item.setPay(doubleValue(item.getInterest()+item.getPrincipal()))
                item.setResidual(doubleValue(residual-item.getPrincipal()))
                item.setFee(0d)
                item.setRemark(null)

                list.add(item)

                residual = item.getResidual()
            }

            if(item!=null){
                item.setPrincipal(doubleValue(item.getPrincipal() + residual))
                item.setPay(doubleValue(item.getPrincipal() + item.getInterest()))
                item.setResidual(0d)
            }

            return list
        }

        return null
    }

    protected static Double doubleValue(Double value){
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
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
