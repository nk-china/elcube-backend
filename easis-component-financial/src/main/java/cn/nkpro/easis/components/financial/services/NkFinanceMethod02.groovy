package cn.nkpro.easis.components.financial.services

import cn.nkpro.easis.co.NkAbstractApplyCSO
import cn.nkpro.easis.co.spel.NkSpELManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.math.RoundingMode

@Component("NkFinanceMethod02")
class NkFinanceMethod02 extends NkAbstractApplyCSO {

    @Autowired
    NkSpELManager spELManager

    @Override
    Object apply(Object options) throws Exception {

        def params  = options as Map

        Double  ir = params.get("ir") as Double
        Integer np = params.get("np") as Integer
        Integer pf = params.get("pf") as Integer
        Double  pv = params.get("pv") as Double
        Double  fv = params.get("fv") as Double
        Long    dt = params.get("dt") as Long

        if (ir != null && np != null && pf != null && pv != null && fv != null && dt != null){

            double residual = pv

            Calendar calendar = Calendar.getInstance()

            // 期间利率 = 年利率 / 付款频率
            Double ip = ir / pf

            // 付款间隔 单位月
            int step = (int) (12 / pf)

            List<PaymentI> list = new ArrayList<>()
            PaymentI item
            for(int i=0;i<np;i++){

                calendar.setTimeInMillis(dt*1000)
                calendar.add(Calendar.MONTH, step*(i+1))
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
